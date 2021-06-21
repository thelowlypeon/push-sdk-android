package com.vibes.vibes;

interface PersonManagerInterface {
    void setCurrent(Person person);

    Person getCurrent();
}

class PersonManager implements PersonManagerInterface {

    private LocalStorage storage;

    PersonManager(LocalStorage storage) {
        this.storage = storage;
    }

    @Override
    public void setCurrent(Person person) {
        if (person == null) {
            this.storage.remove(LocalObjectKeys.currentPerson);
        } else {
            this.storage.put(LocalObjectKeys.currentPerson, person);
        }
    }

    @Override
    public Person getCurrent() {
        return this.storage.get(LocalObjectKeys.currentPerson);
    }
}
