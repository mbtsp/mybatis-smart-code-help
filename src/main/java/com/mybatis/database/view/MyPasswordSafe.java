package com.mybatis.database.view;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.credentialStore.OneTimeString;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.mybatis.model.CacheModel.Cache.DataConfigSourceDto;
import kotlin.jvm.JvmOverloads;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyPasswordSafe {
    @Nullable
    public OneTimeString getPassword(@NotNull DataConfigSourceDto dataSource) {
        return getPassword(dataSource, null);
    }


    public void setPassword(@NotNull DataConfigSourceDto dataSource, @Nullable String password) {
        setPassword(dataSource, password, null);
    }

    private CredentialAttributes createCredentialAttributes(DataConfigSourceDto dataSource, String prefix) {
        String str = "DB";
        StringBuilder sb = new StringBuilder();
        if (prefix == null) {
            prefix = "";
        }
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName(str, sb.append(prefix).append(dataSource.getMyUniqueId()).toString()), null, null, false, false);
    }

    @Nullable
    public Credentials getImpl(@NotNull CredentialAttributes attributes) {
        return getPasswordSafe().get(attributes);
    }

    public void setImpl(@NotNull CredentialAttributes attributes, @Nullable Credentials credentials, boolean useMasterKey) {
        getPasswordSafe().set(attributes, credentials, !useMasterKey);
    }

    @NotNull
    public PasswordSafe getPasswordSafe() {
        return PasswordSafe.Companion.getInstance();
    }

    @JvmOverloads
    public void setPassword(@NotNull DataConfigSourceDto dataSource, @Nullable String password, @Nullable String prefix) {
        Credentials credentials;
        CredentialAttributes createCredentialAttributes = createCredentialAttributes(dataSource, prefix);
        if (password != null) {
            credentials = new Credentials(dataSource.getUserName(), password);
            setImpl(createCredentialAttributes, credentials, true);
            return;
        }
        setImpl(createCredentialAttributes, null, true);
    }

    @Nullable
    public OneTimeString getPassword(@NotNull DataConfigSourceDto dataSource, @Nullable String prefix) {
        Credentials credentials = this.getImpl(this.createCredentialAttributes(dataSource, prefix));
        return credentials != null ? credentials.getPassword() : null;
    }
}
