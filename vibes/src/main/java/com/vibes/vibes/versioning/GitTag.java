package com.vibes.vibes.versioning;

import java.util.Collection;
import java.util.TreeSet;

public class GitTag implements Comparable<GitTag> {
    private String name;

    public GitTag() {
    }

    public GitTag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(GitTag other) {
        if (this.getName() == null || this.getName().isEmpty()) {
            return -1;
        }
        if (other.getName() == null || other.getName().isEmpty()) {
            return 1;
        }
        return other.getName().compareTo(this.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitTag gitTag = (GitTag) o;
        return name.equals(gitTag.name);
    }

    @Override
    public String toString() {
        return "GitTag{" +
                "name='" + name + '\'' +
                '}';
    }

    public static class GitTags {
        private final Collection<GitTag> list = new TreeSet<>();

        public Collection<GitTag> getList() {
            return list;
        }

        public GitTag getCurrent() {
            if (list.iterator().hasNext()) {
                return list.iterator().next();
            }
            return null;
        }
    }
}
