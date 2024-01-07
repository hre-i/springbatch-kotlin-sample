# Spring Batch 3.1.6 + Kotlin 1.9.21 + JPA のサンプル

https://github.com/spring-guides/gs-batch-processing を元に Kotlin に書き直して、
いろいろと書き足してみた。

## Reader/Processor/Writer

### JPA の Repository を使って読んで/処理して/更新する例

- BatchUpdatePeopleConfig
- JPA を使う場合、transactionManager には PlatformTransactionManager を使う．

### CSV を読み込んで，処理して，JDBC を使ってDBに追加する例

- BatchImportPerson
- PersonItemProcessor
- JDBC のみを使う場合，transactionManager には DataSourceTransactionManager が使える．

## Tasklet と引数の取得方法の例

- MyTasklet
- BatchTaskletConfig
    - @Bean をつけたものと、@Component をつけたものを、競合しないようにする。

## Tasklet でメッセージを表示するだけの例

- BatchHelpMessageConfig

## application.properties

```
spring.job.name: ${job.name:helpMessage}
```

起動時に --job.name=ジョブ名 と指定することで、 起動するジョブを変更する。
指定されていない場合は、helpMessage ジョブを実行する。

## 引数

- 引数を受け取るクラスに、`@Component` と `@StepScope` アノテーションをつける。

- コンストラクタで引数をとる場合。

  nullable にした場合、引数がなければ null がはいる。

   ```kotlin
   @Component
   @StepScope
   class MyTasklet(
     @Value("#{jobParameters['arg1']}")
     private val arg1: String,
     @Value("#{jobParameters['arg2']}")
     private val arg2: String?
   ) : Tasklet {
     // ...
   }
   ```

- 属性でとる場合。

  lateinit をつけると nullable にはできないため、引数が指定されていない場合は例外が投げられる。

  ```kotlin
   @Component
   @StepScope
   class MyTasklet : Tasklet {

    @Value("#{jobParameters['arg1']}")
    private lateinit var arg1: String

    // ...
  }
  ```
