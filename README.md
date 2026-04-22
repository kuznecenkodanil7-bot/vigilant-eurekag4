# ChatTabs v2 for Fabric 1.21.1

Вторая версия клиентского мода с вкладками чата и фильтрацией сообщений.

Что добавлено в v2:
- более стабильная сборка под Fabric 1.21.1;
- готовый GitHub Actions workflow для сборки jar;
- вкладки: All, Global, Private, Party, Trade, System;
- русские и английские шаблоны для private/trade;
- команды `/chattabs reload`, `/chattabs list`, `/chattabs next`, `/chattabs prev`, `/chattabs set <index>`;
- сохранение активной вкладки;
- переключение колесом мыши в открытом чате.

Конфиг создаётся автоматически:
- `config/chattabs.json`
- `config/chattabs-state.json`

Сборка:
```bash
./gradlew build
```

GitHub Actions:
- пушишь проект в репозиторий;
- workflow сам собирает jar;
- готовый файл скачивается из Artifacts.
