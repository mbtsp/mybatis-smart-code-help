package com.mybatis.generatorSql.command;

import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.enums.AppendTypeEnum;

import java.util.Optional;

public class FieldSuffixAppendTypeService implements AppendTypeCommand {

    private final SyntaxAppender syntaxAppender;

    /**
     * Instantiates a new Field suffix append type service.
     *
     * @param syntaxAppender the syntax appender
     */
    public FieldSuffixAppendTypeService(final SyntaxAppender syntaxAppender) {

        this.syntaxAppender = syntaxAppender;
    }

    @Override
    public Optional<SyntaxAppender> execute() {
        if (this.syntaxAppender.getType() == AppendTypeEnum.SUFFIX) {
            return Optional.of(this.syntaxAppender);
        }
        return Optional.empty();
    }
}
