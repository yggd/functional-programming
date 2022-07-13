package org.yggd;


import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yggd.Functions.*;

/**
 * 関数型プログラミングとJavaのダサい現実。
 */
public class FunctionJavaTest {

    /**
     * 関数（というかJavaのstaticメソッド）の動作確認。
     */
    @Test
    void testFunction() {
        // 恒等写像
        assertThat(id(12345)).isEqualTo(12345);
        // +1するだけ
        assertThat(g(33211)).isEqualTo(33212);
        // 文字列つけるだけ
        assertThat(h(2)).isEqualTo("文字列:2");
        // 関数合成
        assertThat(h(g(3))).isEqualTo("文字列:4");
    }

    /**
     * ファンクター(関手)則のテスト。
     * map()メソッドの動作確認。
     */
    @Test
    void testFunctor() {

        var op = Optional.of(3);
        // var op = Optional.<Integer>empty(); // 仮にOptionalがemptyでも成立する。
        var st = Stream.of(1, 2, 3, 4, 5);
        var li = List.of(1, 2, 3, 4, 5);
        //var li = List.<Integer>of();

        // ルール1. fmap id == id
        // 恒等関数は値や状態を変化させない。(副作用が起きないこと)
        assertThat( op.map(Functions::id) ).isEqualTo(op);
        assertThat( st.map(Functions::id)).isNotEqualTo(st); // Streamはここで脱落(not equalだからテストは通る)
        assertThat( li.stream().map(Functions::id).collect(Collectors.toList()) )
                .isEqualTo(li); // 上記よりstreamどうしで評価できない、からcollectでListに戻している。ダサい。

        // ルール2. fmap (g . h) == (fmap g) . (fmap h)
        // 関数を合成した後ファンクターに食わせたものは、それぞれの関数をファンクターに食わせた後で
        // 合成した結果と変わらない
        // 注意：Javaでは g . h という関数合成はmap()メソッドのチェーンになるため、
        // 適用の順序が map(h).map(g)と逆になる点に注意。
        assertThat( op.map(x -> h(g(x))) ).isEqualTo( op.map(Functions::g).map(Functions::h) );
        assertThat( li.stream().map(x -> h(g(x))).collect(Collectors.toList()) )
                .isEqualTo( li.stream().map(Functions::g).map(Functions::h).collect(Collectors.toList()) );
            // streamどうしで評価できない、からcollectでListに戻している。ダサい。
    }

    /**
     * モナド則のテスト。
     * flatMap()メソッドの動作確認。
     */
    @Test
    void testMonad() {

        // 準備 関数 f,g,id の結果をOptionalにしたいだけ。
        final Function<Integer, Optional<Integer>> gOp = (Integer i) -> Optional.of(g(i));
        final Function<Integer, Optional<String>> hOp = (Integer i) -> Optional.of(h(i));
        final Function<Integer, Optional<Integer>> idOp = (Integer i) -> Optional.of(id(i));

        var op = Optional.of(3);
        //var op = Optional.<Integer>empty();

        // ルール1. return a >>= h == h a
        // (return aにより)値aをモナド化してから関数hに食わせたものは、関数の戻り値h(x)をモナド化したものと同じ
        // Javaでの >>= 演算子はflatMap()に置き換えて検証する。
        // 注意：JavaのflatMapでは 「m >>= h」という、「モナドを返さない関数hをモナドに食わせる」という表現ができないので、
        //      flatMap()内で関数fの戻り値をOptionalにしてごまかしている。
        assertThat( op.flatMap(gOp) )
                .isEqualTo( op.map(Functions::g) );

        // ルール2. m >>= return == m
        // モナド値をモナド化するということは元のモナド値と同じ(モナドは構造上ネストしない）
        // というよりもこの要請を守るためにflatMap()が定義されている。
        assertThat( op.flatMap(Optional::of) ).isEqualTo(op);

        // ルール3. (m >>= g) >>= h == m >>= (\x -> g x >>= h)
        // モナドmを関数gに食わせた後で関数hに食わせたものは、
        // 関数の戻り値g(x)の結果をモナド化して関数hに食わせる「関数」を、モナドmに食わせたものと同じ。
        // ラムダ式がごちゃるので一見わかりづらいが、左辺のうち「関数gの結果をモナド化して関数hを食わせる関数」は
        // ((>>= h) . g) から (g >=> h) で書き換えることができ、これは下記と同じである。
        // (m >>= g) >>= h == m >>= (g >=> h)
        // つまり代数的に言い換えると (a*b)*c == a*(b*c)という結合法則を満たすことを言いたい。
        // (関数とモナドという異なるものを結合させているので表記がややこしいが、関数の実行順序を変えているわけではない）
        assertThat( op.flatMap(gOp).flatMap(hOp) )
                .isEqualTo( op.flatMap(i -> gOp.apply(i).flatMap(hOp)) );

        // ルール4. モナドはファンクター則を満たす。
        // おさらい。fmapはflatMapに読み替えて検証する。

        // ルール4-1. fmap id = id
        assertThat( op.flatMap(idOp) ).isEqualTo( op );
        // ルール4-2. fmap (g . h) == (fmap g) . (fmap h)
        // 注意：flatMapによるメソッドチェーンは、(fmap g) . (fmap h) が flatMap(h).flatMap(g)と表記が逆になる。
        // (関数はg->hという順番で呼び出される意図は同じ)
        assertThat( op.flatMap(i -> hOp.apply(g(i))) )
                .isEqualTo( op.flatMap(gOp).flatMap(hOp) );
    }
}
