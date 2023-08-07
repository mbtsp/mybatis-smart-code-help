package com.mybatis.generatorSql.command;

import com.mybatis.generatorSql.appender.SyntaxAppender;

import java.util.Optional;

/**
 * The type Join append type command.
 */
public class JoinAppendTypeCommand implements AppendTypeCommand {

    private final SyntaxAppender syntaxAppender;

    /**
     * Instantiates a new Join append type command.
     *
     * @param syntaxAppender the syntax appender
     */
    public JoinAppendTypeCommand(final SyntaxAppender syntaxAppender) {

        this.syntaxAppender = syntaxAppender;
    }

    @Override
    public Optional<SyntaxAppender> execute() {
        return Optional.of(this.syntaxAppender);
    }
}
