package studios.hawkeyegame.hackweekbot.chatbot;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Brain implements Serializable {
    public transient int LIMIT;
    public transient Random RANDOM;

    private transient Cache<String, List<String>> CHALLENGES;
    private transient String lastUser;
    private transient Long lastTime;
    private transient String lastMessage;

    public static final String WORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    public static final String END_CHARS = ".!?⏎";
    public static final List<String> COMMON_WORDS = Lists.newArrayList(
            "the", "of", "to", "and", "a", "in", "is", "it", "you", "that", "he", "was", "for", "on", "are", "with",
            "as", "I", "his", "they", "be", "at", "one", "have", "this", "from", "or", "had", "by", "no", "but", "some",
            "what", "there", "we", "can", "out", "other", "were", "all", "your", "when", "up", "use", "yes", "hot"
    );

    private Map<String, Quad> QUADS = Maps.newConcurrentMap();
    private Map<String, Set<String>> WORDS = Maps.newConcurrentMap();
    private Map<String, Set<String>> NEXT = Maps.newConcurrentMap();
    private Map<String, Set<String>> PREVIOUS = Maps.newConcurrentMap();

    public Brain(int wordLimit) {
        refresh(wordLimit);
    }

    public void addDocument(String uri) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(uri).openStream()));
        String str = "";
        int ch;
        while ((ch = reader.read()) != -1) {
            str += ch;
            if (END_CHARS.indexOf((char) ch) >= 0) {
                String sentence = str;
                sentence = sentence.replace('\r', ' ');
                sentence = sentence.replace('\n', ' ');
                add(sentence);
                str = "";
            }
        }
        add(str);
        reader.close();
    }

    public List<String> add(String sentence) {
        if (WORDS.size() >= LIMIT) {
            return null;
        }

        List<String> quads = Lists.newArrayList();
        List<String> parts = Lists.newArrayList();
        sentence = sentence.trim();
        char[] chars = sentence.toCharArray();

        boolean punctuation = false;
        String str = "";

        for (char ch : chars) {
            if ((WORD_CHARS.indexOf(ch) >= 0) == punctuation) {
                punctuation = !punctuation;
                if (str.length() > 0) {
                    parts.add(str);
                }
                str = "";
            }
            str += ch;
        }
        if (str.length() > 0) {
            parts.add(str);
        }

        if (parts.size() >= 4) {
            for (int i = 0; i < parts.size() - 3; i++) {
                Quad quad = new Quad(parts.get(i), parts.get(i + 1), parts.get(i + 2), parts.get(i + 3));
                if (QUADS.containsKey(quad.getId())) {
                    quad = QUADS.get(quad.getId());
                } else if (quad.isValid()) {
                    QUADS.put(quad.getId(), quad);
                } else {
                    continue;
                }
                quads.add(quad.getId());

                if (i == 0) {
                    quad.setCanStart(true);
                }

                if (i == parts.size() - 4) {
                    quad.setCanEnd(true);
                }

                for (int j = 0; j < 4; j++) {
                    String token = parts.get(i + j);
                    if (!WORDS.containsKey(token)) {
                        WORDS.put(token, new HashSet<>(1));
                    }
                    WORDS.get(token).add(quad.getId());
                }

                if (i > 0) {
                    String previousToken = parts.get(i - 1);
                    if (!PREVIOUS.containsKey(quad.getId())) {
                        PREVIOUS.put(quad.getId(), new HashSet<>(1));
                    }
                    PREVIOUS.get(quad.getId()).add(previousToken);
                }

                if (i < parts.size() - 4) {
                    String nextToken = parts.get(i + 4);
                    if (!NEXT.containsKey(quad.getId())) {
                        NEXT.put(quad.getId(), new HashSet<>(1));
                    }
                    NEXT.get(quad.getId()).add(nextToken);
                }
            }
        }
        return quads;
    }

    public List<String> challenge(String name) {
        List<List<String>> pack = getSentence();
        CHALLENGES.put(name, pack.get(0));
        List<String> sentence = pack.get(1);
        sentence.set(0, "@" + name + " " + sentence.get(0));
        return sentence;
    }

    public List<String> getReply(String name, String statement, boolean learn) {
        if (statement != null) {
            List<Quad> given = searchForQuads(statement).stream().map(QUADS::get).collect(Collectors.toList());
            List<String> quadIds = new ArrayList<>();
            LinkedList<String> parts = new LinkedList<>();

            if (given.size() < 2) {
                List<List<String>> sentence = getSentence();
                if (sentence == null || sentence.size() < 2) {
                    return Collections.singletonList("beep. boop. beep.");
                }
                return getSentence().get(1);
            }

            List<Quad> quads = new ArrayList<>(getRelated(given));
            boolean tooSmall = quads.size() < 4;
            if (tooSmall) {
                quads = new ArrayList<>(QUADS.values());
            }

            if (quads.isEmpty()) {
                return new ArrayList<>();
            }

            Quad middleQuad = quads.get(RANDOM.nextInt(quads.size()));
            Quad quad = middleQuad;

            for (int i = 0; i < 4; i++) {
                parts.add(quad.getToken(i));
            }

            while (!quad.canEnd()) {
                quadIds.add(quad.getId());
                List<String> nextTokens = new ArrayList<>(NEXT.get(quad.getId()));
                String nextToken = nextTokens.get(RANDOM.nextInt(nextTokens.size()));
                quad = QUADS.get(new Quad(quad.getToken(1), quad.getToken(2), quad.getToken(3), nextToken).getId());
                parts.add(nextToken);
            }

            quad = middleQuad;
            while (!quad.canStart()) {
                quadIds.add(quad.getId());
                List<String> previousTokens = new ArrayList<>(PREVIOUS.get(quad.getId()));
                String previousToken = previousTokens.get(RANDOM.nextInt(previousTokens.size()));
                quad = QUADS.get(new Quad(previousToken, quad.getToken(0), quad.getToken(1), quad.getToken(2)).getId());
                parts.addFirst(previousToken);
            }

            List<String> sentence = new ArrayList<>();
            StringBuilder part = new StringBuilder("@" + name + " ");
            for (String token : parts) {
                part.append(token);
                if (token.contains("⏎")) {
                    sentence.add(part.toString().trim());
                    part = new StringBuilder();
                }
            }
            if (!"".equals(part.toString())) {
                sentence.add(part.toString());
            }

            if (learn) {
                if (!tooSmall && CHALLENGES.asMap().containsKey(name)) {
                    List<String> related = CHALLENGES.asMap().get(name);
                    for (String relatedId : related) {
                        Quad relatedQuad = QUADS.get(relatedId);
                        for (Quad found : given) {
                            relatedQuad.addRelated(found.getId());
                            found.addRelated(relatedQuad.getId());
                            QUADS.put(relatedId, relatedQuad);
                            QUADS.put(found.getId(), found);
                        }
                    }
                    CHALLENGES.put(name, quadIds);
                } else {
                    add(statement);
                    CHALLENGES.put(name, quadIds);
                }
            }

            return sentence;
        }

        return getSentence().get(1);
    }

    public List<List<String>> getSentence() {
        return getSentence(null);
    }

    public List<List<String>> getSentence(String word) {
        if (word != null && COMMON_WORDS.contains(word.toLowerCase())) {
            return getSentence();
        }

        List<String> quadIds = new ArrayList<>();
        LinkedList<String> parts = new LinkedList<>();

        List<Quad> quads;
        if (word != null && WORDS.containsKey(word)) {
            quads = WORDS.get(word).stream().map(QUADS::get).collect(Collectors.toList());
        }
        else {
            quads = new ArrayList<>(QUADS.values());
        }

        if (quads.isEmpty()) {
            return new ArrayList<>();
        }

        Quad middleQuad = quads.get(RANDOM.nextInt(quads.size()));
        Quad quad = middleQuad;

        for (int i = 0; i < 4; i++) {
            parts.add(quad.getToken(i));
        }

        while (!quad.canEnd()) {
            quadIds.add(quad.getId());
            List<String> nextTokens = new ArrayList<>(NEXT.get(quad.getId()));
            String nextToken = nextTokens.get(RANDOM.nextInt(nextTokens.size()));
            quad = QUADS.get(new Quad(quad.getToken(1), quad.getToken(2), quad.getToken(3), nextToken).getId());
            parts.add(nextToken);
        }

        quad = middleQuad;
        while (!quad.canStart()) {
            quadIds.add(quad.getId());
            List<String> previousTokens = new ArrayList<>(PREVIOUS.get(quad.getId()));
            String previousToken = previousTokens.get(RANDOM.nextInt(previousTokens.size()));
            quad = QUADS.get(new Quad(previousToken, quad.getToken(0), quad.getToken(1), quad.getToken(2)).getId());
            parts.addFirst(previousToken);
        }

        List<String> sentence = new ArrayList<>();
        String part = "";
        for (String token : parts) {
            part += token;
            if (token.contains("⏎")) {
                sentence.add(part.trim());
                part = "";
            }
        }
        if (!"".equals(part)) {
            sentence.add(part);
        }

        List<List<String>> pack = new ArrayList<>();
        pack.add(quadIds);
        pack.add(sentence);
        return pack;
    }

    public List<String> searchForQuads(String sentence) {
        List<String> quads = new ArrayList<>();
        List<String> parts = new ArrayList<>();
        sentence = sentence.trim();
        char[] chars = sentence.toCharArray();

        boolean punctuation = false;
        String str = "";

        for (char ch : chars) {
            if ((WORD_CHARS.indexOf(ch) >= 0) == punctuation) {
                punctuation = !punctuation;
                if (str.length() > 0) {
                    parts.add(str);
                }
                str = "";
            }
            str += ch;
        }
        if (str.length() > 0) {
            parts.add(str);
        }

        if (parts.size() >= 4) {
            for (int i = 0; i < parts.size() - 3; i++) {
                Quad quad = new Quad(parts.get(i), parts.get(i + 1), parts.get(i + 2), parts.get(i + 3));
                Optional<Quad> existing = QUADS.values().stream().filter(quad::nearMatch).findAny();
                existing.ifPresent(value -> quads.add(value.getId()));
            }
        }
        return quads;
    }

    public Long getLastTime() {
        return lastTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastUser() {
        return lastUser;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
    }

    public void purge() {
        WORDS.clear();
        QUADS.clear();
        NEXT.clear();
        PREVIOUS.clear();
    }

    public void refresh(int wordLimit) {
        LIMIT = wordLimit;
        CHALLENGES = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.SECONDS).build();
        RANDOM = new Random();
        lastUser = "";
        lastTime = 0L;
        lastMessage = "";
    }

    public List<Quad> getRelated(List<Quad> start) {
        Set<String> quads = start.stream().map(Quad::getId).collect(Collectors.toSet());
        for (Quad quad : start) {
            quads.addAll(quad.getRelated());
            for (String related : quad.getRelated()) {
                Quad relatedQuad = QUADS.get(related);
                quads.addAll(relatedQuad.getRelated());
            }
        }
        return quads.stream().map(QUADS::get).collect(Collectors.toList());
    }

    public int size() {
        return QUADS.size();
    }
}
