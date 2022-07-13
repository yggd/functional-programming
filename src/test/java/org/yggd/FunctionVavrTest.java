package org.yggd;

import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yggd.Functions.*;

/**
 * Vavrの力を借りてみる。
 */
public class FunctionVavrTest {

    /**
     * ファンクター(関手)則のテスト。
     * map()メソッドの動作確認。
     */
    @Test
    void testFunctor() {
        final Option<Integer> op = Option.of(3);
//        final Option<Integer> op = Option.none();
        final Stream<Integer> st = Stream.of(1, 2, 3, 4, 5);
//        final Stream<Integer> st = Stream.empty();
        final Either<Exception, Integer> et = Either.right(3);
//        final Either<Exception, Integer> et = Either.left(new NullPointerException("ぬるぽ"));


        // ルール1. fmap id == id
        assertThat( op.map(Functions::id) ).isEqualTo(op);
        assertThat( st.map(Functions::id) ).isEqualTo(st);
        assertThat( et.map(Functions::id) ).isEqualTo(et);

        // ルール2. fmap (g . h) == (fmap g) . (fmap h)
        assertThat( op.map(x -> h(g(x))) ).isEqualTo( op.map(Functions::g).map(Functions::h) );
        assertThat( st.map(x -> h(g(x))) ).isEqualTo( st.map(Functions::g).map(Functions::h) );
        assertThat( et.map(x -> h(g(x))) ).isEqualTo( et.map(Functions::g).map(Functions::h) );
    }

    /**
     * モナド則のテスト.
     */
    @Test
    void testMonad() {
        final Option<Integer> op = Option.of(3);
//        final Option<Integer> op = Option.none();

        final Stream<Integer> st = Stream.of(1, 2, 3, 4, 5);
//        final Stream<Integer> st = Stream.empty();

        final Either<Exception, Integer> et = Either.right(3);
//        final Either<Exception, Integer> et = Either.left(new NullPointerException("ぬるぽ"));

        final Function<Integer, Option<Integer>> gOp  = (Integer i) -> Option.of(g(i));
        final Function<Integer, Option<String>> hOp   = (Integer i) -> Option.of(h(i));
        final Function<Integer, Option<Integer>> idOp = (Integer i) -> Option.of(id(i));

        final Function<Integer, Stream<Integer>> gSt  = (Integer i) -> Stream.of(g(i));
        final Function<Integer, Stream<String>> hSt   = (Integer i) -> Stream.of(h(i));
        final Function<Integer, Stream<Integer>> idSt = (Integer i) -> Stream.of(id(i));

        final Function<Integer, Either<Exception, Integer>> gEt  = (Integer i) -> Either.right(g(i));
        final Function<Integer, Either<Exception, String>> hEt   = (Integer i) -> Either.right(h(i));
        final Function<Integer, Either<Exception, Integer>> idEt = (Integer i) -> Either.right(id(i));

        // ルール1. return a >>= h == h a
        assertThat( op.flatMap(x -> Option.of(g(x)))    ).isEqualTo( op.map(Functions::g) );
        assertThat( st.flatMap(x -> Stream.of(g(x)))    ).isEqualTo( st.map(Functions::g) );
        assertThat( et.flatMap(x -> Either.right(g(x))) ).isEqualTo( et.map(Functions::g) );

        // ルール2. m >>= return == m
        assertThat( op.flatMap(Option::of)    ).isEqualTo( op );
        assertThat( st.flatMap(Stream::of)    ).isEqualTo( st );
        assertThat( et.flatMap(Either::right) ).isEqualTo( et );

        // ルール3. (m >>= g) >>= h == m >>= (\x -> g x >>= h)
        assertThat( op.flatMap(gOp).flatMap(hOp) )
                .isEqualTo( op.flatMap(i -> gOp.apply(i).flatMap(hOp)) );
        assertThat( st.flatMap(gSt).flatMap(hSt) )
                .isEqualTo( st.flatMap(i -> gSt.apply(i).flatMap(hSt)) );
        assertThat( et.flatMap(gEt).flatMap(hEt) )
                .isEqualTo( et.flatMap(i -> gEt.apply(i).flatMap(hEt)) );

        // ルール4-1. fmap id = id
        assertThat( op.flatMap(idOp) ).isEqualTo( op );
        assertThat( st.flatMap(idSt) ).isEqualTo( st );
        assertThat( et.flatMap(idEt) ).isEqualTo( et );

        // ルール4-2. fmap (g . h) == (fmap g) . (fmap h)
        assertThat( op.flatMap(i -> hOp.apply(g(i))) )
                .isEqualTo( op.flatMap(gOp).flatMap(hOp) );
        assertThat( st.flatMap(i -> hSt.apply(g(i))) )
                .isEqualTo( st.flatMap(gSt).flatMap(hSt) );
        assertThat( et.flatMap(i -> hEt.apply(g(i))) )
                .isEqualTo( et.flatMap(gEt).flatMap(hEt) );
    }

}
