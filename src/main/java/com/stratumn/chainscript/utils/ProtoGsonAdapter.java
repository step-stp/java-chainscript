/*
  Copyright 2017 Stratumn SAS. All rights reserved.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.stratumn.chainscript.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.util.JsonFormat;

public class ProtoGsonAdapter<T extends Message> extends TypeAdapter<T> {

    Class<T> messageClass;

    public ProtoGsonAdapter(Class<T> messageClass) {
        super();
        this.messageClass = messageClass;
    }

    /**
     * Override the read method to return a Link object from it's json
     * representation.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T read(JsonReader jsonReader) throws IOException {

        T.Builder messageBuilder;
        try {
            messageBuilder = (Builder) (messageClass.getDeclaredMethod("newBuilder").invoke(null));

            // Use the JsonFormat class to parse the json string into the builder object
            // The Json string will be parsed from the JsonReader object
            JsonParser jsonParser = new JsonParser();
            JsonFormat.parser().merge(jsonParser.parse(jsonReader).toString(), messageBuilder);
            // Return the built @Link message
            return (T) messageBuilder.build();
        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new IOException(e);
        }
    }

    /**
     * Override the write method and set the json value of the Link message.
     */
    @Override
    public void write(JsonWriter jsonWriter, T message) throws IOException {
        // Call the printer of the JsonFormat class to convert the proto message to Json
        jsonWriter.jsonValue(JsonFormat.printer().print(message));
    }
}
