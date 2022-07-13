# 今更Java(+α)で関数型プログラミング(FP)ってどうなの？

* 結論：Java 17 現在でも Java SE だけだとキツい(不完全）。
* NGワード：Scalaでやれ。

Java 8から登場した `Optional` や `Stream` のように、いわゆる関数型プログラミング(FP)への適応としていくつかのAPIが実装されてからかなり時間が経っているが、
有効活用されているところをあまり見たことがない。
実情としては巷でNPEが発生しまくっているし、FP適用により軽減されるだろうと予見されていたスレッドアンセーフ問題も相変わらず起きている。  
これはJavaの言語仕様やAPIの整備が不十分なのか、単に利用者がついていけてないだけなのか、などなど検証の切り口は色々あると考えるが、
Java 17 が大手を振って使えるようになった現在、めでたくFPとも関係が深い switch 式による型判定やガード条件（パターンマッチング）が実装された良い機会でもあるので、
改めてJavaの各種APIやFP対応ライブラリが具体的なルールである "ファンクタ(関手）則"、"モナド則" に準拠しているのかを改めて見直そうという試みをしたい。
ていうか、ここら辺は今まで気分で生きていてちゃんと検証してなかった。

### 検証対象
* Java SE : `Optional`, `Stream`
* Vavr : `Option`, `Stream`, `Either`
* Λrrow(kotlinのFPライブラリ) : `Option`, `List(nonEmptyList)`, `Either`

### 検証環境
* OpenJDK 17.0.2
* Kotlin 1.6.10 (kotlin-maven-plugin)

### 検証方法

* Mavenを使って試験を一括実行するか、IDEでテストケースを個別に実行してください。

```shell
$ mvn clean test
```

### 検証結果

* ○ : ファンクタ則、モナド則共に準拠。
* × : 準拠せず。
* \- : 未実装。

| ライブラリ   | `Option` or `Optional` | `Stream` or `List` | `Either` |
|---------|------------------------|---------------|----------|
| Java SE | ○                      | × (ファンクタ則でNG) | -        |
| Vavr    | ○                      | ○             | ○        |
| Λrrow   | ○                      | ○             | ○        |

### 参考情報

* Functor Laws https://wiki.haskell.org/Typeclassopedia#Laws
* Monad Laws https://wiki.haskell.org/Monad_laws
* Vavr https://www.vavr.io/
* Λrrow https://arrow-kt.io/
