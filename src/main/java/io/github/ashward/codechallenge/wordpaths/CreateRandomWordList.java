package io.github.ashward.codechallenge.wordpaths;

import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CreateRandomWordList {

  public static void main(String[] args) {
    new CreateRandomWordList().writeWordList();
  }

  public void writeWordList() {
    try (ObjectInputStream ois = new ObjectInputStream(this.getClass().getResourceAsStream("/50kwords.obj"))) {
      Map<Integer, Set<String>> words = (Map<Integer, Set<String>>) ois.readObject();

      File file;
      try (FileWriter fw = new FileWriter(new File("./loadsawords.txt"))) {
        Random rand = new Random();

        for (int i = 0; i < 100000; ++i) {

          int length = rand.nextInt(6) + 3;

          List<String> thing = new ArrayList<>(words.get(length));

          String word1 = thing.get(rand.nextInt(thing.size()));
          String word2 = thing.get(rand.nextInt(thing.size()));

          fw.write(word1 + " " + word2 + "\n");
        }
      }

      return;
    } catch (Exception e) {
      System.err.println("Error while loading serialised words");
    }
  }


}
