# Design Note / 設計管理ドキュメント

## 1. Overview / 概要
A learning support application that manages English words and Japanese translations in a card format, allowing users to toggle visibility to help memorize words.
英単語と日本語訳をカード形式でリスト管理し、表示・非表示を切り替えながら単語を覚えるための学習支援アプリ。

---

## 2. Feature List / 機能一覧

| # | Feature / 機能 | Category / カテゴリ | Status / 実装状態 |
|---|---|---|---|
| 1 | Add cards with English word and Japanese translation / 英単語と日本語訳を入力してカードを追加 | Word Management / 単語管理 | Implemented / 実装済み |
| 2 | Delete cards permanently from DB / カードをDBから完全削除 | Word Management / 単語管理 | Implemented / 実装済み |
| 3 | Hide individual cards by swiping right (Keep in DB) / 右スワイプでカードを個別非表示（DBには残す） | Word Management / 単語管理 | Implemented / 実装済み |
| 4 | Toggle visibility of all English words / 全カードの英単語を一括で隠す／表示 | Display Toggle / 表示切り替え | Pending / 未実装 |
| 5 | Toggle visibility of all Japanese meanings / 全カードの日本語訳を一括で隠す／表示 | Display Toggle / 表示切り替え | Pending / 未実装 |
| 6 | Restore all hidden (isVisible=false) cards / 非表示にしたカードを全てリストに復元 | Word Management / 単語管理 | Pending / 未実装 |
| 7 | Input Validation (Empty check, Duplicate prevention) / 入力バリデーション（空文字・重複防止） | UX / 入力UX | Pending / 未実装 |

---

## 3. Screen Design / 画面設計

### Overall Layout / 全体レイアウト
```
┌──────────────────────────────────────────┐
│ English Learning App (Title)             │
├──────────────────────────────────────────┤
│ [English Input (Error message)]          │
│ [Japanese Input (Error message)]         │
│ [Add Card Button]                        │
├──────────────────────────────────────────┤
│ [Hide English] [Hide Japanese] [Restore] │  ← #4 #5 #6 Buttons
├──────────────────────────────────────────┤
│ ← Swipe to hide (#3)                     │
│ ┌──────────────────────────────────────┐ │
│ │ Apple                                │ │  ← English (#4)
│ │ りんご                                │ │  ← Japanese (#5)
│ │                        [Delete]      │ │  ← #2 Delete
│ └──────────────────────────────────────┘ │
│ ...                                      │
└──────────────────────────────────────────┘
```

### Card & Button Specifications / 仕様詳細
- **Toggle Buttons (#4, #5):** Labels change between "Hide XXX" and "Show XXX". This state is UI-only and not saved in DB.
  トグルボタン（#4, #5）: ラベルは「XXXを隠す」「XXXを表示」で切り替わる。この状態はUI上のみで保持し、DBには保存しない。
- **Restore Button (#6):** Updates all `isVisible` flags in DB to `true`.
  復元ボタン（#6）: DB内の全ての `isVisible` フラグを `true` に更新する。

---

## 4. Input Validation & UX / 入力バリデーションとUX (#7)

| Item / 項目 | Rule & Behavior / ルールと挙動 |
|---|---|
| **Empty Check / 空文字チェック** | If either English or Japanese is empty, the add button is disabled or an error message is shown. / いずれかが空の場合、追加ボタンを無効化、またはエラーを表示する。 |
| **Duplicate Prevention / 重複登録防止** | Prevent registration if the English word already exists in the DB. / すでに登録済みの英単語を追加しようとした場合、警告を出してブロックする。 |
| **Post-Add Behavior / 追加後の挙動** | Clear fields and return focus to the English input. / 登録成功時、入力をクリアし英単語欄にフォーカスを戻す。 |
| **Keyboard Control / キーボード制御** | Hide keyboard after clicking "Add" or tapping outside. / 追加ボタン押下後、または画面外タップ時にキーボードを閉じる。 |

---

## 5. Data Design / データ設計

### Word Entity (Room Database)
- **Database Name:** `app_database`
- **Table Name:** `words`

| Field / フィールド | Type / 型 | Default / デフォルト | Description / 説明 |
|---|---|---|---|
| id | Int | Auto-generate | Primary Key / 主キー |
| englishWord | String | - | English word / 英単語 |
| japaneseMeaning | String | - | Japanese meaning / 日本語訳 |
| isVisible | Boolean | true | Card visibility flag / カード自体の表示フラグ |

---

## 6. Change History / 変更履歴

| Date / 日付 | Content / 内容 |
|---|---|
| 2026-03-11 | Initial version / 初版作成 |
| 2026-03-11 | Implemented swipe-to-hide / 右スワイプによる個別カード非表示機能を実装 |
| 2026-03-11 | Merged docs and added restore/validation design / ドキュメントの統合、復元・バリデーション設計の追加 |

