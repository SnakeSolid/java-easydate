# EasyDate Bot

A simple Telegram bot designed to simplify conversations on dating websites.
The bot can suggest phrases to start a conversation based on a photo from a
profile or how to continue the dialogue.

![Bot usage example](images/easydate.png)

## Build

Clone repository and start following command:

```sh
mvn package assembly:single
```

## Features

- [x] Suggest profile description;
- [x] Generate openers by photo;
- [x] Generate openers by photo and profile description;
- [ ] Suggest next message for chat;
- [ ] Analyze dialog.

## Usage

Start application using following command:

```sh
java -jar target/easydate-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
  bot \
  --bot-token "TOKEN" \
  --allow-user ID1 \
  --allow-user ID2 \
  --allow-user ID3
```

Where `TOKEN` is telegram bot token. `ID*` owners user id, can be set to -1 bot
will send user id in reply message.

To analyze single image and return openers for it. Use following command:

```sh
java -jar target/easydate-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
  image \
  --image IMAGE_PATH
```

## LLM Settings

By default, the local ollama server located at `http://localhost:11434/`
is used. The `llava-llama3` model is used for image description, and the
`gemma2` model for text generation. These settings can be changed using
the configuration file (option `--config`).

Example of configuration file with default settings:

```
uri.image   = "http://localhost:11434/"
uri.text    = "http://localhost:11434/"
model.image = "llava-llama3"
model.text  = "gemma2"
timeout     = 120
```

## License

Source code is primarily distributed under the terms of the MIT license. See LICENSE for details.
