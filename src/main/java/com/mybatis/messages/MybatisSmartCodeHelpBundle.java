package com.mybatis.messages;

import com.intellij.DynamicBundle;
import com.mybatis.state.MybatisSettingsState;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.Locale;
import java.util.function.Supplier;

public class MybatisSmartCodeHelpBundle extends DynamicBundle {
    public static final String BUNDLE = "messages.MybatisSmartCodeHelp";

    public MybatisSmartCodeHelpBundle() {
        super(BUNDLE);

    }

    public MybatisSmartCodeHelpBundle(String resourceBundle) {
        super(resourceBundle);
    }

    public static MybatisSmartCodeHelpBundle INSTANCE() {
        MybatisSettingsState mybatisSettingsState = MybatisSettingsState.getInstance();
        if (mybatisSettingsState != null && mybatisSettingsState.getState() != null && StringUtils.isNotBlank(mybatisSettingsState.getState().getLanguage())) {
            String language = mybatisSettingsState.getState().getLanguage();
            if (language.equals("zh")) {
                Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
            } else if (language.equals("en")) {
                Locale.setDefault(Locale.US);
            } else if (language.equals("default")) {
                Locale.setDefault(Locale.getDefault());
            } else {
                Locale.setDefault(Locale.getDefault());
            }
        }
        return new MybatisSmartCodeHelpBundle(BUNDLE);
    }

    @NotNull
    @Nls
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE().getMessage(key, params);
    }


    @NotNull
    @Nls
    public static String messageOr(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @Nls @NotNull String defaultValue, Object... params) {
        return INSTANCE().messageOrDefault(key, defaultValue, params);
    }


    @NotNull
    public static Supplier<String> messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE().getLazyMessage(key, params);
    }

}
