# タスク管理

## ステータス凡例
`[ ]` 未着手 　`[>]` 進行中 　`[x]` 完了

---

## A. `isVisible` → `isShown` リネーム

> 全タスクの基盤。最初に完了させること。

- [ ] A-1: `Word.kt` — `val isVisible` を `val isShown` にリネーム
- [ ] A-2: `WordDao.kt` — クエリ `WHERE isVisible = 1` を `WHERE isShown = 1` に修正
- [ ] A-3: `WordViewModel.kt` — `word.copy(isVisible = false)` を `word.copy(isShown = false)` に修正
- [ ] A-4: `AppDatabase.kt` — `version = 1` → `version = 2` に更新し、Migration を追加
  - `ALTER TABLE words RENAME COLUMN isVisible TO isShown`

---

## E. 未使用コードの削除

> A 完了後に実施。

- [ ] E-1: `WordDao.kt` — `searchWords()` を削除
- [ ] E-2: `WordDao.kt` — `deleteAll()` を削除

---

## C. #6 非表示カード全復元

- [ ] C-1: `WordDao.kt` — `restoreAll()` を追加
  - `@Query("UPDATE words SET isShown = 1") suspend fun restoreAll()`
- [ ] C-2: `WordViewModel.kt` — `restoreAllWords()` を追加
- [ ] C-3: `fragment_first.xml` — 「非表示をすべて戻す」ボタンを追加
- [ ] C-4: `FirstFragment.kt` — ボタン押下で `wordViewModel.restoreAllWords()` を呼ぶ

---

## D. #7 入力バリデーション強化

- [ ] D-1: `WordDao.kt` — 重複チェック用クエリを追加
  - `SELECT COUNT(*) FROM words WHERE LOWER(englishWord) = LOWER(:english)`
- [ ] D-2: `WordViewModel.kt` — `addWord()` に重複チェックを追加し、結果（成功 / 重複）を Fragment に通知する
- [ ] D-3: `FirstFragment.kt` — 重複時に `TextInputLayout.error` へ「既に登録されています」を表示
- [ ] D-4: `FirstFragment.kt` — 追加成功後にキーボードを閉じる
- [ ] D-5: `FirstFragment.kt` — 追加成功後に英単語入力欄へフォーカスを戻す

---

## B. #4/#5 テキスト一括トグル

- [ ] B-1: `WordViewModel.kt` — `isEnglishHidden: LiveData<Boolean>` と `toggleEnglishHidden()` を追加
- [ ] B-2: `WordViewModel.kt` — `isJapaneseHidden: LiveData<Boolean>` と `toggleJapaneseHidden()` を追加
- [ ] B-3: `WordAdapter.kt` — `bind()` に `hideEnglish` / `hideJapanese` フラグを追加し、テキストの visibility を `INVISIBLE` / `VISIBLE` で制御
- [ ] B-4: `fragment_first.xml` — 「英単語を隠す」「日本語訳を隠す」ボタンを追加
- [ ] B-5: `FirstFragment.kt` — `isEnglishHidden` / `isJapaneseHidden` を observe してアダプターに反映
- [ ] B-6: `FirstFragment.kt` — ボタン押下で toggle を呼び、ラベルをフラグに応じて切り替える

---

## 実装順序

```
A（リネーム） → E（削除） → C（#6復元） → D（#7バリデーション） → B（#4/#5トグル）
```

## 変更履歴

| 日付 | 内容 |
|---|---|
| 2026-04-18 | 初版作成 |
