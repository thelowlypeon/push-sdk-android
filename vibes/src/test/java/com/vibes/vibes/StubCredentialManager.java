package com.vibes.vibes;

class StubCredentialManager implements CredentialManagerInterface {
    private Credential credential;

    public StubCredentialManager() {

    }

    public StubCredentialManager(Credential credential) {
        this.credential = credential;
    }

    @Override
    public void setCurrent(Credential credential) {
        this.credential = credential;
    }

    @Override
    public Credential getCurrent() {
        return credential;
    }
}