# GraalVM ネイティブイメージ検討メモ

## 検討結果
- Ktor 3 系では Netty エンジンのネイティブサポートが限定的であり、GraalVM 向けには CIO エンジンへ切り替える前提となる。
- `io.ktor.plugin` の `graalvmNative` 設定と Oracle の Reachability Metadata Repository を併用すれば、Ktor 本体のリフレクション設定を大部分自動化できる。
- MySQL Connector/J 9.4.0 は GraalVM 対応済みで、JDBC 接続自体はネイティブビルド後もサポートされる。
- Exposed は大量のリフレクションを利用するため追加設定の負荷が高く、ORM の見直しも検討余地がある。

## 主な課題
- Netty 依存から CIO へのエンジン差し替え、および `EngineMain` 起動コードの改修。
- Exposed・JDBC 周りのリフレクション/リソース登録を `reflect-config.json` 等で整備する必要がある。
- Logback 初期化、`application.yaml` などのリソース登録、サービスローダー設定など Native Image 固有の調整項目が多い。
- Dockerfile を JRE 実行前提からネイティブバイナリ前提へ再設計する必要がある。

## 推奨対応
- `build.gradle.kts` に `graalvmNative`（または `ktor.nativeImage`）ブロックを追加し、`--initialize-at-build-time` や `--enable-url-protocols=mysql` などのオプションを整理する。
- CIO エンジンへ移行し、`ktor-server-netty` 依存を削除して `ktor-server-cio` を導入する。
- `native-image-agent` を用いた実行で必要なメタデータを収集し、Exposed・MySQL・Logback 向けに反映する。
- リソース自動検出や `graalvmNative.resources.autodetect()` を活用し、設定ファイルやテンプレートがバイナリに含まれるよう管理する。

## 次のアクション
1. Gradle 設定を更新し、CIO エンジン移行後に `./gradlew :ulid-sequencer:nativeCompile` が通るかを確認する。
2. `native-image-agent` を組み込んだ JVM 実行でワークロードを再現し、生成されたメタデータをビルドへ取り込む。
3. 生成バイナリを軽量ベースイメージでコンテナ化し、MySQL 接続や起動時間などを評価する。
4. 本番要件を満たすパフォーマンス・監視の検証結果を踏まえ、導入可否を判断する。

## ORM の調査
- **Micronaut Data**：アノテーション処理と Ahead-of-Time 設計でリフレクションレスを実現し、GraalVM Native Image と親和性が高い。DI も Micronaut 上で最適化される。
- **Doma**：Quarkus 向け拡張などで公式にネイティブ対応を表明しており、コンパイル時コード生成型のため追加メタデータがほぼ不要。
- **jOOQ**：DSL ベースでリフレクション使用が少ないが、コード生成物の管理とネイティブビルド用の登録処理が必要。Quarkus 拡張など既存事例がある。
- **Hibernate ORM**：ソロ利用でのネイティブ対応は未整備で、リフレクション設定が多岐に渡るため候補から外すのが無難。
