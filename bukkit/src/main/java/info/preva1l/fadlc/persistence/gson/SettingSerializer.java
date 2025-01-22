package info.preva1l.fadlc.persistence.gson;

import com.google.gson.*;
import info.preva1l.fadlc.models.user.settings.Setting;

import java.lang.reflect.Type;

public class SettingSerializer implements JsonSerializer<Setting<?>>, JsonDeserializer<Setting<?>> {
    private static final String STATE_FIELD = "state";
    private static final String CLASS_FIELD = "class";

    @Override
    public JsonElement serialize(Setting<?> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty(CLASS_FIELD, src.getClass().getName());
        json.add(STATE_FIELD, context.serialize(src.getState()));
        return json;
    }

    @Override
    public Setting<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String className = jsonObject.get(CLASS_FIELD).getAsString();
        try {
            Class<?> clazz = Class.forName(className);
            if (!Setting.class.isAssignableFrom(clazz)) {
                throw new JsonParseException("Class " + className + " is not a valid Setting type.");
            }

            @SuppressWarnings("unchecked")
            Setting<Object> settingInstance = (Setting<Object>) clazz.getDeclaredConstructor().newInstance();

            Object state = context.deserialize(jsonObject.get(STATE_FIELD), settingInstance.getState().getClass());
            settingInstance.setState(state);

            return settingInstance;
        } catch (Exception e) {
            throw new JsonParseException("Error deserializing Setting: " + e.getMessage(), e);
        }
    }
}