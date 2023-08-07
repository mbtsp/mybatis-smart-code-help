package com.mybatis.generator;

import java.util.Collection;

public abstract class AbstractGenerateModel {

    public static final AbstractGenerateModel START_WITH_MODEL = new StartWithModelAbstract();

    public static final AbstractGenerateModel END_WITH_MODEL = new EndWithModelAbstract();

    public static final AbstractGenerateModel CONTAIN_MODEL = new ContainModelAbstract();

    public static AbstractGenerateModel getInstance(String identifier) {
        try {
            return getInstance(Integer.parseInt(identifier));
        } catch (Exception e) {
            return START_WITH_MODEL;
        }
    }

    public static AbstractGenerateModel getInstance(int identifier) {
        switch (identifier) {
            case 0:
                return START_WITH_MODEL;
            case 1:
                return END_WITH_MODEL;
            case 2:
                return CONTAIN_MODEL;
            default:
                throw new AssertionError();
        }
    }

    public boolean matchesAny(String[] patterns, String target) {
        for (String pattern : patterns) {
            if (apply(pattern, target)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesAny(Collection<String> patterns, String target) {
        return matchesAny(patterns.toArray(new String[patterns.size()]), target);
    }

    protected abstract boolean apply(String pattern, String target);

    public abstract int getIdentifier();

    static class StartWithModelAbstract extends AbstractGenerateModel {

        @Override
        protected boolean apply(String pattern, String target) {
            return target.startsWith(pattern);
        }

        @Override
        public int getIdentifier() {
            return 0;
        }
    }

    static class EndWithModelAbstract extends AbstractGenerateModel {

        @Override
        protected boolean apply(String pattern, String target) {
            return target.endsWith(pattern);
        }

        @Override
        public int getIdentifier() {
            return 1;
        }
    }

    static class ContainModelAbstract extends AbstractGenerateModel {

        @Override
        protected boolean apply(String pattern, String target) {
            return target.contains(pattern);
        }

        @Override
        public int getIdentifier() {
            return 2;
        }
    }
}
