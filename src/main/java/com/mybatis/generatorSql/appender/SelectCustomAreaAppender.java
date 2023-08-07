package com.mybatis.generatorSql.appender;

import com.mybatis.generatorSql.SyntaxAppenderFactory;
import com.mybatis.generatorSql.mapping.AreaSequence;

public class SelectCustomAreaAppender extends CustomAreaAppender {


    public SelectCustomAreaAppender(final String area, final String areaType, final SyntaxAppenderFactory syntaxAppenderFactory) {
        super(area, areaType, AreaSequence.AREA, AreaSequence.RESULT, syntaxAppenderFactory);
    }

}
