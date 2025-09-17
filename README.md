# finance-manager

## Getting start

1. `mise.local.toml` を作成
   - `HOME_FINANCE_MANAGER_BOT_TOKEN` の値を設定
2. ビルドし、docker 実行
   ```shell
   ./gradlew clean build -x test
   docker compose up --build -d
   ```

## 本番実行

1. `mise.local.toml` に以下の値を設定
   - `HOME_FINANCE_MANAGER_CHANNEL_ID`
2. ビルドし、docker 実行
   ```shell
   ./gradlew clean build -x test
   docker compose -f compose.prd.yaml -f compose.yaml up --build -d
   ```

## Discord Bot の設定

https://discord.com/developers/applications

### Application の Bot 設定

1. Token を作成
   - `mise.local.toml` の `HOME_FINANCE_MANAGER_BOT_TOKEN` に設定
2. `Message Content Intent` のラジオボタンを有効に

### サーバーに Bot を追加

1. Application の OAuth2 から招待 URL を作成
   - `OAuth2 URL Generator` は以下にチェックを入れる
     - Scopes: Bot
     - Bot Permissions: SendMessage
2. URL をブラウザに貼り付け、Bot を使いたいサーバーに招待
