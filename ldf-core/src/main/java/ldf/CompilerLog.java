package ldf;

import ldf.java_cup.runtime.LocationAwareEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Cristian Harja
 */
public class CompilerLog {

    private SortedSet<Entry> messages;
    private SortedSet<Entry> readOnlyMessages;

    public static final Comparator<Entry> ENTRY_COMPARATOR
            = new Comparator<Entry>() {
        @Override
        public int compare(Entry o1, Entry o2) {
            int x1, x2;

            x1 = (o1.pos == null) ? -1 : o1.pos.getOffsetL();
            x2 = (o2.pos == null) ? -1 : o2.pos.getOffsetL();
            if (x1 != x2) return x1 - x2;

            if (x1 == -1) {
                return 0;
            }

            x1 = o1.pos.getOffsetR();
            x2 = o2.pos.getOffsetR();
            if (x1 != x2) return x1 - x2;

            x1 = o1.hashCode();
            x2 = o2.hashCode();

            return x1 - x2;
        }
    };

    public CompilerLog() {
        messages = new TreeSet<Entry>(ENTRY_COMPARATOR);
        readOnlyMessages = Collections.unmodifiableSortedSet(messages);
    }

    public void printLog(PrintStream out) {
        for (Entry e : messages) {
            out.println(e);
        }
    }

    public void logMessage(
            @Nullable String fileName,
            @Nonnull String format,
            @Nonnull EntryType type,
            @Nullable LocationAwareEntity pos,
            @Nonnull Object[] args
    ) {
        Entry res = new Entry();
        res.fileName = fileName;
        res.pos = pos;
        res.type = type;
        res.msgFormat = format;
        res.msgArgs = args;

        messages.add(res);
    }

    public SortedSet<Entry> getMessages() {
        return readOnlyMessages;
    }

    private static void formatLocation(
            Appendable out,
            String fileName,
            LocationAwareEntity pos
    ) throws IOException {
        if (fileName == null) {
            if (pos != null) {
                out.append("<unknown>");
            }
        } else {
            out.append(fileName);
        }
        if (pos != null) {
            out.append(":");
            out.append(String.valueOf(pos.getLineL()));
            out.append(":");
            out.append(String.valueOf(pos.getColumnL()));
            if (pos.getOffsetL() < pos.getOffsetR()) {
                out.append("-");
                out.append(String.valueOf(pos.getColumnR()));
            }
        }
    }

    public enum EntryType {
        ERROR, WARN
    }

    @SuppressWarnings("unused")
    public class Entry {
        @Nullable
        private LocationAwareEntity pos;

        @Nonnull
        private EntryType type;

        @Nullable
        private String fileName;

        @Nonnull
        private String msgFormat;

        @Nonnull
        private Object[] msgArgs;

        private String formattedLocation;

        private String formattedMessage;

        @Nullable
        public LocationAwareEntity getPosition() {
            return pos;
        }

        @Nonnull
        public EntryType getType() {
            return type;
        }

        @Nullable
        public String getFileName() {
            return fileName;
        }

        public String getMessageFormat() {
            return msgFormat;
        }

        public Object[] getMessageArgs() {
            return msgArgs.clone();
        }

        public String getFormattedLocation() {
            if (formattedLocation != null) {
                return formattedLocation;
            }
            synchronized (this) {
                if (formattedLocation != null) {
                    return formattedLocation;
                }
                try {
                    StringBuilder sb = new StringBuilder();
                    formatLocation(sb, fileName, pos);
                    formattedLocation = sb.toString();
                } catch (IOException ignored) {
                }
            }
            return formattedLocation;
        }

        public String getFormattedMessage() {
            if (formattedMessage != null) {
                return formattedLocation;
            }
            synchronized (this) {
                if (formattedMessage != null) {
                    return formattedLocation;
                }
                formattedMessage = MessageFormat.format(msgFormat, msgArgs);
            }
            return formattedMessage;
        }

        public String toString() {
            String result = "[" + type + "] ";
            result += (type == EntryType.ERROR) ? "" : " ";
            return result +
                    getFormattedLocation() + ": " +
                    getFormattedMessage();
        }
    }

}