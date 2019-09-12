package io.mycat.sqlparser.util.simpleParser2;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class GroupPatternBuilder {
    private final IdRecorder idRecorder;
    private final UTF8Lexer utf8Lexer;
    private final DFG dfg;

    public GroupPatternBuilder() {
        this(Collections.emptyMap());
    }

    public GroupPatternBuilder(Map<String, Object> keywords) {
        this.idRecorder = new IdRecorderImpl(true);
        ((IdRecorderImpl) this.idRecorder).load(keywords);
        this.utf8Lexer = new UTF8Lexer(idRecorder);
        this.dfg = new DFG.DFGImpl();
    }

    public void addRule(String pattern) {
        addRule(StandardCharsets.UTF_8.encode(pattern));
    }

    public void addRule(byte[] buffer) {
        addRule(ByteBuffer.wrap(buffer));
    }

    public void addRule(ByteBuffer buffer) {
        utf8Lexer.init(buffer, 0, buffer.limit());
        dfg.addRule(new Iterator<Seq>() {
            @Override
            public boolean hasNext() {
                return utf8Lexer.nextToken();
            }

            @Override
            public Seq next() {
                return idRecorder.createConstToken(null);
            }
        });
    }

    public GroupPattern createGroupPattern() {
        return new GroupPattern(dfg,idRecorder.createCopyRecorder());
    }
}