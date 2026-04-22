# ChatTabs for Fabric 1.21.11

Клиентский мод для Minecraft **1.21.11** с вкладками чата и фильтрацией сообщений в стиле LabyMod.

## Что умеет

- отдельные вкладки чата;
- фильтрация по `contains`, `regex` и типам сообщений;
- отдельный цвет для каждой вкладки;
- сохранение активной вкладки между перезапусками;
- переключение вкладок колесом мыши в открытом чате;
- быстрая перезагрузка конфига через кнопку `Reload` или команду `/chattabs`.

## Команды

- `/chattabs` — перезагрузить конфиг;
- `/chattabs next` — следующая вкладка;
- `/chattabs prev` — предыдущая вкладка;
- `/chattabs set <index>` — выбрать вкладку по индексу.

## Конфиг

После первого запуска создаётся файл:

```text
config/chattabs.json
```

Также отдельно сохраняется выбранная вкладка:

```text
config/chattabs-state.json
```

### Пример конфига

```json
{
  "tabs": [
    {
      "name": "All",
      "all": true,
      "color": "white",
      "contains": [],
      "regex": [],
      "excludesContains": [],
      "excludesRegex": [],
      "messageTypes": [],
      "caseSensitive": false,
      "matchAllRules": false
    },
    {
      "name": "PM",
      "all": false,
      "color": "light_purple",
      "contains": [],
      "regex": ["(?i)whispers to you", "(?i)from ", "(?i)to "],
      "excludesContains": [],
      "excludesRegex": [],
      "messageTypes": ["private"],
      "caseSensitive": false,
      "matchAllRules": false
    },
    {
      "name": "Trade",
      "all": false,
      "color": "gold",
      "contains": [],
      "regex": ["(?i)\\b(buy|sell|wts|wtb|trade|auction|market|shop|продам|куплю|обмен)\\b"],
      "excludesContains": [],
      "excludesRegex": [],
      "messageTypes": ["trade"],
      "caseSensitive": false,
      "matchAllRules": false
    }
  ],
  "maxStoredMessages": 2000,
  "buttonWidth": 72,
  "buttonHeight": 18,
  "buttonGap": 3,
  "buttonsY": 4,
  "scrollToSwitchTabs": true,
  "invertScroll": false,
  "showReloadButton": true
}
```

## Доступные типы сообщений

- `chat`
- `private`
- `party`
- `trade`
- `system`
- `game_info`
- `global`
- `unknown`

## Цвета вкладок

Используются стандартные значения `Formatting`, например:

- `white`
- `yellow`
- `gold`
- `green`
- `aqua`
- `blue`
- `light_purple`
- `red`
- `gray`

## Сборка

```bash
./gradlew build
```

Готовый jar появится в:

```text
build/libs/
```

## Замечания

Это клиентский мод. Для разных серверов шаблоны чата могут отличаться, поэтому фильтры `regex` и `contains` лучше подстраивать под формат конкретного сервера.
