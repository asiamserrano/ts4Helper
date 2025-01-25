package org.example.ts4package.models;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.util.Arrays;

import static org.example.ts4package.constants.StringConstants.*;
import static org.example.ts4package.constants.StringConstants.EMPTY;

@Slf4j
public class MessageModel {

    public static class JSON extends JSONObject {

        @SuppressWarnings(UNCHECKED)
        public JSON put(Keys keys, Object value) {
            this.put(keys.name(), value);
            return this;
        }

        public Object get(Keys keys) {
            String object = this.get(keys.name()).toString();
            if (object == null) {
                return null;
            } else {
                if (keys == Keys.directoryFilePath) {
                    return object;
                } else {
                    return WebsiteModel.Builder.parse(object);
                }
            }
        }

        @Override
        public String toString() {
            return this.toJSONString().replaceAll(BACK_SLASHES, EMPTY);
        }

    }

    @AllArgsConstructor
    public static class Builder {

        private final String filePath;
        private final WebsiteModel.Builder builder;

        public Builder(File file, WebsiteModel websiteModel) {
            this.filePath = file.getAbsolutePath();
            this.builder = websiteModel.builder();
        }

        private Builder(JSON json) {
            this.filePath = json.get(Keys.directoryFilePath).toString();
            this.builder = (WebsiteModel.Builder) json.get(Keys.websiteModelBuilder);
        }

        public static Builder parse(String message) {
            try {
                JSON json = new JSON();
                JSONObject jsonObject = (JSONObject) JSONValue.parse(message);
                Arrays.stream(Keys.values()).forEach(key -> json.put(key, jsonObject.get(key.toString())));
                return new Builder(json);
            } catch (Exception e) {
                log.error("unable to parse JSON object {}: {}", message, e.getMessage());
                throw new RuntimeException(e);
            }
        }

        public MessageModel build() {
            try {
                File file = new File(this.filePath);
                WebsiteModel model = this.builder.build();
                return new MessageModel(file, model);
            } catch (Exception e) {
                log.error("error when building model: {}", e.getMessage());
                return null;
            }
        }

        @Override
        public String toString() {
            return this.toJSONModel().toString();
        }

        protected JSON toJSONModel() {
            return new JSON()
                    .put(Keys.directoryFilePath, this.filePath)
                    .put(Keys.websiteModelBuilder, this.builder.toJSONModel());
        }

    }

    public enum Keys {
        directoryFilePath, websiteModelBuilder
    }

    public final File directory;
    public final WebsiteModel websiteModel;

    private MessageModel(File directory, WebsiteModel websiteModel) {
        this.directory = directory;
        this.websiteModel = websiteModel;
    }

    @Override
    public String toString() {
        return this.builder().toString();
    }

    protected Builder builder() {
        String filePath = this.directory.getAbsolutePath();
        WebsiteModel.Builder builder = this.websiteModel.builder();
        return new Builder(filePath, builder);
    }

}
