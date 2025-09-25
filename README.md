# finance-manager

## Getting start

1. `mise.local.toml` を作成
   - `BOT_TOKEN` の値を設定
2. ビルドし、docker 実行
   ```shell
   ./gradlew clean build -x test
   docker compose up --build -d
   ```

## 本番実行

1. `mise.local.toml` に以下の値を設定
   - `CHANNEL_ID`
   - `DATASOURCE_USERNAME`
   - `DATASOURCE_PASSWORD`
2. docker 実行
   ```shell
   docker compose -f compose.prd.yaml up -d
   ```

## Discord Bot の設定

https://discord.com/developers/applications

### Application の Bot 設定

1. Token を作成
   - `mise.local.toml` の `BOT_TOKEN` に設定
2. `Message Content Intent` のラジオボタンを有効に

### サーバーに Bot を追加

1. Application の OAuth2 から招待 URL を作成
   - `OAuth2 URL Generator` は以下にチェックを入れる
     - Scopes: Bot
     - Bot Permissions: SendMessage
2. URL をブラウザに貼り付け、Bot を使いたいサーバーに招待
