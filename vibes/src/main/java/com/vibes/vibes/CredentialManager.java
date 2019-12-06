package com.vibes.vibes;

/**
 * An interface for being considered a CredentialManager.
 */
interface CredentialManagerInterface {
    /**
     * Sets the currently-in-use {@link Credential}.
     * @param credential the Credential to store
     */
    void setCurrent(Credential credential);

    /**
     * Gets the currently-in-use {@link Credential}.
     */
    Credential getCurrent();
}

/**
 * A basic Credential Manager; it maintains the current {@link Credential} in {@link LocalStorage}.
 */
class CredentialManager implements CredentialManagerInterface {
    /**
     * The storage to use for long-term persistence of {@link Credential} objects.
     */
    private LocalStorage storage;

    /**
     * Initialize this object.
     * @param storage a {@link LocalStorage} to use for long-term credential storage.
     */
    CredentialManager(LocalStorage storage) {
        this.storage = storage;
    }

    /**
     * Sets the currently-in-use {@link Credential}.
     * @param credential the Credential to store
     */
    public void setCurrent(Credential credential) {
        if (credential == null) {
            this.storage.remove(LocalObjectKeys.currentCredential);
        } else {
            this.storage.put(LocalObjectKeys.currentCredential, credential);
        }
    }

    /**
     * Gets the currently-in-use {@link Credential}.
     * @return the Credential, or null.
     */
    public Credential getCurrent() {
        return this.storage.get(LocalObjectKeys.currentCredential);
    }
}