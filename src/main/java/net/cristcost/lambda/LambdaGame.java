/*
 * Copyright 2015, Cristiano Costantini
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.cristcost.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("boxing")
public class LambdaGame {

  // this class runs at least with Java 8
  public static void main(String[] args) {
    howManyConsonants();
    howManyConsonantsWithoutTypeInference();
    howManyConsonantsWithName();
    eachStepOfHowManyConsonants();
    howManyConsonantsWithMethodReferences();
    howManyConsonantsPseudoJava7();
    howManyConsonantsPseudoJava7Inlineless();
    howManyConsonantsJava7();
  }

  // consider a list of strings
  static List<String> list =
      Arrays.asList("Cristiano", "Michele", "Sergio", "Giuseppe", "Stefano");

  // lets count with lambda how many consonants in all the names
  public static void howManyConsonants() {
    
    Integer out = list.stream() // process with a stream
        .map((p) -> p.replaceAll("[aeiou]", "")) // strip all (lower case) vowels from each name  
        .map((a) -> a.length()) // count the length of the remaining word
        .reduce((a, b) -> a + b) // sum each of the previously computed lengths
        .get(); // get the result

    System.out.println("howManyConsonants: there are " + out + " consonants");
  }

  // that without type inference looks like
  public static void howManyConsonantsWithoutTypeInference() {
    Integer out = list.stream()
        .map((String p) -> p.replaceAll("[aeiou]", ""))
        .map((String a) -> a.length())
        .reduce((Integer a, Integer b) -> a + b)
        .get();

    System.out.println("howManyConsonantsWithoutTypeInference: there are " + out + " consonants");
  }

  // we have used the following @FunctionalInterfaces
  static BinaryOperator<Integer> accumulator = (a, b) -> a + b;
  static Function<String, Integer> lengthMapper = (a) -> a.length();
  static Function<String, String> devowelizerMapper = (p) -> p.replaceAll("[aeiou]", "");

  // and we can rewrite it using these above
  public static void howManyConsonantsWithName() {
    Integer out = list.stream()
        .map(devowelizerMapper)
        .map(lengthMapper)
        .reduce(accumulator)
        .get();

    System.out.println("howManyConsonantsWithName: there are " + out + " consonants");
  }

  // we have used "streams" to process the output, these are the individual
  // steps
  public static void eachStepOfHowManyConsonants() {

    Stream<String> stream = list.stream();
    Stream<String> devowelizedStream = stream.map(devowelizerMapper);
    Stream<Integer> lengthStream = devowelizedStream.map(lengthMapper);
    Optional<Integer> reduce = lengthStream.reduce(accumulator);
    Integer out = reduce.get();

    System.out.println("eachStepOfHowManyConsonants: there are " + out + " consonants");
  }

  // the lambda function we have used are equivalent to the methods in this
  // class
  public static class LambdaMethods {

    public static Integer accumulator(Integer a, Integer b) {
      return a + b;
    };

    public static Integer lengthMapper(String s) {
      return s.length();
    };

    public static String devowelizerMapper(String s) {
      return s.replaceAll("[aeiou]", "");
    };
  }

  // they are so equivalent that we can implement the methods using them
  public static class MethodsUsingLambdasImpl {

    public static Integer accumulator(Integer t, Integer u) {
      return accumulator.apply(t, u);
    };

    public static Integer lengthMapper(String s) {
      return lengthMapper.apply(s);
    };

    public static String devowelizerMapper(String s) {
      return devowelizerMapper.apply(s);
    };
  }

  // methods of a class can be now referenced and used in place of a lambda
  public static void howManyConsonantsWithMethodReferences() {
    Integer out = list.stream()
        .map(LambdaMethods::devowelizerMapper)
        .map(LambdaMethods::lengthMapper)
        .reduce(LambdaMethods::accumulator)
        .get();

    System.out.println("howManyConsonantsWithMethodReferences: there are " + out + " consonants");
  }

  // if we don't have java 8? we would have used anonymous classes
  public static void howManyConsonantsPseudoJava7() {
    Integer out =
        list.stream().map(new Function<String, String>() {

          @Override
          public String apply(String p) {
            return p.replaceAll("[aeiou]", "");
          }
        }).map(new Function<String, Integer>() {

          @Override
          public Integer apply(String a) {
            return a.length();
          }
        }).reduce(new BinaryOperator<Integer>() {

          @Override
          public Integer apply(Integer a, Integer b) {
            return a + b;
          }
        }).get();

    System.out.println("howManyConsonantsPseudoJava7: there are " + out + " consonants");
  }

  // let's rewrite it without inlining anonymous classes
  static Function<String, String> anonymousDevowelizerMapperClass = new Function<String, String>() {

    @Override
    public String apply(String p) {
      return p.replaceAll("[aeiou]", "");
    }
  };
  static Function<String, Integer> anonymousLengthMapperClass = new Function<String, Integer>() {

    @Override
    public Integer apply(String a) {
      return a.length();
    }
  };
  static BinaryOperator<Integer> anonymousAccumulatorClass = new BinaryOperator<Integer>() {

    @Override
    public Integer apply(Integer a, Integer b) {
      return a + b;
    }
  };

  // and then re-implement without the inline anonymous classes
  public static void howManyConsonantsPseudoJava7Inlineless() {
    Integer out = list.stream()
        .map(anonymousDevowelizerMapperClass)
        .map(anonymousLengthMapperClass)
        .reduce(anonymousAccumulatorClass)
        .get();

    System.out.println("howManyConsonantsPseudoJava7Inlineless: there are " + out + " consonants");
  }

  // we haven't used lambda but we are still using Java 8 streams, what's without?
  public static void howManyConsonantsJava7() {
    LambdaGameWithoutLambda.main(null);
  }
}
