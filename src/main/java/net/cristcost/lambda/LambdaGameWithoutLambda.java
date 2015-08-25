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

import net.cristcost.lambda.LambdaGameWithoutLambda.SmallJava8.BinaryOperator;
import net.cristcost.lambda.LambdaGameWithoutLambda.SmallJava8.Function;
import net.cristcost.lambda.LambdaGameWithoutLambda.SmallJava8.Optional;
import net.cristcost.lambda.LambdaGameWithoutLambda.SmallJava8.Stream;

import static net.cristcost.lambda.LambdaGameWithoutLambda.SmallJava8.streamFromList;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("boxing")
public class LambdaGameWithoutLambda {

  // this class runs also on Java 7
  public static void main(String[] args) {
    howManyConsonantsProcedural();
    howManyConsonantsJava7();
    eachStepOfHowManyConsonantsJava7();
  }

  // consider the same list a list of strings
  static List<String> list =
      Arrays.asList("Cristiano", "Michele", "Sergio", "Giuseppe", "Stefano");

  // lets count how many consonants in all the names
  public static void howManyConsonantsProcedural() {

    Integer out = 0;
    for (String name : list) {
      String consonants = name.replaceAll("[aeiou]", "");
      int length = consonants.length();
      out += length;
    }

    System.out.println("howManyConsonantsProcedural: there are " + out + " consonants");
  }
  
  // implementation without lambda and without java 8 streams
  public static void howManyConsonantsJava7() {

    Integer out = streamFromList(list)
        .map(devowelizerMapperClass)
        .map(lengthMapperClass)
        .reduce(accumulatorClass)
        .get();

    System.out.println("howManyConsonantsJava7: there are " + out + " consonants");
  }

  // and same implementation in each individual step
  public static void eachStepOfHowManyConsonantsJava7() {

    Stream<String> stream = streamFromList(list);
    Stream<String> devowelizedStream = stream.map(devowelizerMapperClass);
    Stream<Integer> lengthStream = devowelizedStream.map(lengthMapperClass);
    Optional<Integer> reduce = lengthStream.reduce(accumulatorClass);
    Integer out = reduce.get();

    System.out.println("eachStepOfHowManyConsonantsJava7: there are " + out + " consonants");
  }

  // the functions used are inline anonymous classes
  static Function<String, String> devowelizerMapperClass = new Function<String, String>() {

    @Override
    public String apply(String p) {
      return p.replaceAll("[aeiou]", "");
    }
  };
  static Function<String, Integer> lengthMapperClass = new Function<String, Integer>() {

    @Override
    public Integer apply(String a) {
      return a.length();
    }
  };
  static BinaryOperator<Integer> accumulatorClass = new BinaryOperator<Integer>() {

    @Override
    public Integer apply(Integer a, Integer b) {
      return a + b;
    }
  };

  // but we don't use Java 8! so here is the small implementation of Streams and Functional Interfaces 
  // that we have used above
  public static class SmallJava8 {

    /**
     * Functional Interface similar to java.util.function.Function
     */
    public interface Function<I, O> {

      /**
       * Applies this function to the given argument
       */
      O apply(I input);
    }

    /**
     * Functional Interface similar to java.util.function.BinaryOperator
     */
    public interface BinaryOperator<T> {

      /**
       * Applies this function to the given argument
       */
      T apply(T param1, T param2);
    }

    /**
     * A sequence of elements supporting aggregate operations that works in a
     * similar way as java.util.stream.Stream
     */
    public interface Stream<T> {

      /**
       * Returns a stream consisting of the results of applying the given
       * function to the elements of this stream.
       */
      <R> Stream<R> map(Function<? super T, ? extends R> mapper);

      /**
       * Performs a reduction on the elements of this stream, using an
       * associative accumulation function, and returns an Optional describing
       * the reduced value, if any.
       */
      T reduce(T identity, BinaryOperator<T> accumulator);

      /**
       * Performs a reduction on the elements of this stream, using the provided
       * identity value and an associative accumulation function, and returns
       * the reduced value.
       */
      Optional<T> reduce(BinaryOperator<T> accumulator);

    }

    /**
     * Implementation of SmallJava8.Stream backed by an array
     */
    public static class ArrayStream<T> implements Stream<T> {

      private final T[] array;

      private ArrayStream(T[] array) {
        this.array = array;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {

        int length = array.length;
        R[] outArray = (R[]) new Object[length];
        for (int i = 0; i < length; i++) {
          outArray[i] = mapper.apply(array[i]);
        }
        return new ArrayStream<R>(outArray);
      }

      @Override
      public T reduce(T identity, BinaryOperator<T> accumulator) {
        T result = identity;
        for (T element : array) {
          result = accumulator.apply(result, element);
        }
        return result;
      }

      @Override
      public Optional<T> reduce(BinaryOperator<T> accumulator) {
        boolean foundAny = false;
        T result = null;
        for (T element : array) {
          if (!foundAny) {
            foundAny = true;
            result = element;
          } else
            result = accumulator.apply(result, element);
        }
        return foundAny ? (new Optional<T>(result)) : (new Optional<T>());
      }
    }

    /**
     * Utility to get a Stream from a java.util.List
     */
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> streamFromList(List<T> list) {
      T[] arr = (T[]) new Object[list.size()];
      arr = list.toArray(arr);
      Stream<T> ret = new ArrayStream<T>(arr);
      return ret;
    }

    /**
     * A container object which may or may not contain a non-null value.
     */
    public static class Optional<T> {

      final T t;

      public Optional(T t) {
        this.t = t;
      }

      public Optional() {
        this.t = null;
      }

      public T get() {
        return t;
      }
    }
  }

}
