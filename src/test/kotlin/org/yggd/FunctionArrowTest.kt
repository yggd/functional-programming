package org.yggd

import arrow.core.*
import arrow.core.Either.*
import org.assertj.core.api.Assertions.assertThat
import org.yggd.Functions.*
import org.junit.jupiter.api.Test

/**
 * Λrrowの力を借りてみる。
 */
class FunctionArrowTest {

    /**
     * ファンクタ則のテスト.
     */
    @Test
    fun testFunctor() {
        val op : Option<Int> = Some(3)
//        val op : Option<Int> = None
        val li : List<Int> = nonEmptyListOf(1, 2, 3, 4, 5)
//        val li : List<Int> = emptyList()
        val et : Either<Exception, Int> = Right(3)
//        val et : Either<Exception, Int> = Left(NullPointerException("ぬるぽ"))

        // ルール1. fmap id == id
        assertThat( op.map { id(it) } ).isEqualTo( op )
        assertThat( li.map { id(it) } ).isEqualTo( li )
        assertThat( et.map { id(it) } ).isEqualTo( et )

        // ルール2. fmap (g . h) == (fmap g) . (fmap h)
        assertThat( op.map { h(g(it)) } ).isEqualTo( op.map { g(it) }.map { h(it) } )
        assertThat( li.map { h(g(it)) } ).isEqualTo( li.map { g(it) }.map { h(it) } )
        assertThat( et.map { h(g(it)) } ).isEqualTo( et.map { g(it) }.map { h(it) } )
    }

    /**
     * モナド則のテスト.
     */
    @Test
    fun testMonad() {

        val op : Option<Int> = Some(3)
        val li : List<Int> = nonEmptyListOf(1, 2, 3, 4, 5)
        val et : Either<Exception, Int> = Right(3)

        val gOp  : (Int) -> Option<Int>    = { Some(g(it))  }
        val hOp  : (Int) -> Option<String> = { Some(h(it))  }
        val idOp : (Int) -> Option<Int>    = { Some(id(it)) }

        val gLi  : (Int) -> List<Int>    = { nonEmptyListOf(g(it))  }
        val hLi  : (Int) -> List<String> = { nonEmptyListOf(h(it))  }
        val idLi : (Int) -> List<Int>    = { nonEmptyListOf(id(it)) }

        val gEt  : (Int) -> Either<Exception, Int>    = { Right(g(it))  }
        val hEt  : (Int) -> Either<Exception, String> = { Right(h(it))  }
        val idEt : (Int) -> Either<Exception, Int>    = { Right(id(it)) }

        // ルール1. return a >>= h == h a
        assertThat( op.flatMap(gOp) ).isEqualTo( op.map { g(it) } )
        assertThat( li.flatMap(gLi) ).isEqualTo( li.map { g(it) } )
        assertThat( et.flatMap(gEt) ).isEqualTo( et.map { g(it) } )

        // ルール2. m >>= return == m
        assertThat( op.flatMap { Some(it) } ).isEqualTo(op)
        assertThat( li.flatMap { nonEmptyListOf(it) } ).isEqualTo(li)
        assertThat( et.flatMap { Right(it) } ).isEqualTo(et)

        // ルール3. (m >>= g) >>= h == m >>= (\x -> g x >>= h)
        assertThat( op.flatMap(gOp).flatMap(hOp) ).isEqualTo( op.flatMap { gOp(it).flatMap(hOp) } )
        assertThat( li.flatMap(gLi).flatMap(hLi) ).isEqualTo( li.flatMap { gLi(it).flatMap(hLi) } )
        assertThat( et.flatMap(gEt).flatMap(hEt) ).isEqualTo( et.flatMap { gEt(it).flatMap(hEt) } )

        // ルール4-1. fmap id = id
        assertThat( op.flatMap(idOp) ).isEqualTo( op )
        assertThat( li.flatMap(idLi) ).isEqualTo( li )
        assertThat( et.flatMap(idEt) ).isEqualTo( et )

        // ルール4-2. fmap (g . h) == (fmap g) . (fmap h)
        assertThat( op.flatMap { hOp(g(it)) } ).isEqualTo( op.flatMap(gOp).flatMap(hOp) )
        assertThat( li.flatMap { hLi(g(it)) } ).isEqualTo( li.flatMap(gLi).flatMap(hLi) )
        assertThat( et.flatMap { hEt(g(it)) } ).isEqualTo( et.flatMap(gEt).flatMap(hEt) )
    }
}