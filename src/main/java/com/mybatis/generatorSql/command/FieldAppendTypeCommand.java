package com.mybatis.generatorSql.command;

import com.mybatis.generatorSql.appender.SyntaxAppender;

import java.util.Optional;

public class FieldAppendTypeCommand implements AppendTypeCommand {
    private final SyntaxAppender syntaxAppender;

    /**
     * Instantiates a new Field append type command.
     *
     * @param syntaxAppender the syntax appender
     */
    public FieldAppendTypeCommand(final SyntaxAppender syntaxAppender) {

        this.syntaxAppender = syntaxAppender;
    }

    @Override
    public Optional<SyntaxAppender> execute() {
        return Optional.of(this.syntaxAppender);
    }
}
