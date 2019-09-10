package io.github.ashward.codechallenge.wordchain.worker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractWorker {
  protected Set<String> sameLengthWords;

  protected String startWord;
  protected String endWord;

  private static ConcurrentHashMap<String, Set<String>> nextWords = new ConcurrentHashMap<>();

  public AbstractWorker(Set<String> sameLengthWords, String startWord, String endWord) {
    this.sameLengthWords = sameLengthWords;

    this.startWord = startWord;
    this.endWord = endWord;
  }

  char[] validChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
          'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\''};

  public abstract List<String> getPath();

  public Set<String> getNextWords(String word) {
    return nextWords.computeIfAbsent(word, (wordToCompute) -> {
      char[] wordChars = wordToCompute.toCharArray();

      HashSet<String> nextWords = new HashSet<>();

      for (int i = 0; i < wordToCompute.length(); ++i) {
        char originalChar = wordChars[i];

        for (char c : validChars) {
          if (c == originalChar) {
            continue;
          }

          wordChars[i] = c;

          String s = String.valueOf(wordChars);

          if (sameLengthWords.contains(s)) {
            nextWords.add(s);
          }
        }

        wordChars[i] = originalChar;
      }

      return nextWords;
    });
  }

//    public Set<String> getNextWords(String word) {
//
//      return nextWords.computeIfAbsent(word, (wordToCompute) ->
//              sameLengthWords.stream().filter(thisWord -> areStringsSingleCharDifferent(thisWord, wordToCompute)).collect(Collectors.toSet())
//      );
//    }

  /**
   * @param string1
   * @param string2
   * @return true if the strings differ by a single character, false otherwise (including if they are equal)
   */
  public boolean areStringsSingleCharDifferent(String string1, String string2) {
    if (string1.equals(string2)) {
      return false;
    }

    boolean oneCharDifferent = false;

    for (int i = 0; i < string1.length(); ++i) {
      if (string1.charAt(i) != string2.charAt(i)) {
        if (oneCharDifferent) {
          return false;
        } else {
          oneCharDifferent = true;
        }
      }
    }

    return true;
  }
}
