package com.ghaph.utils;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/*
    Taken from github.com/PringlePot/LCWebsocket
 */
public class ByteBufWrapper {

    private final ByteBuf buf;

    public ByteBufWrapper(ByteBuf buf) {
        this.buf = buf;
    }

    public void writeVarInt(int b) {
        while ((b & 0xFFFFFF80) != 0x0) {
            this.buf.writeByte((b & 0x7F) | 0x80);
            b >>>= 7;
        }
        this.buf.writeByte(b);
    }

    public int readVarInt() {
        int i = 0;
        int chunk = 0;
        byte b;
        do {
            b = this.buf.readByte();
            i |= (b & 0x7F) << chunk++ * 7;
            if (chunk > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b & 0x80) == 0x80);
        return i;
    }

    public String readStringFromBuffer(int p_150789_1_) throws IOException {
        int var2 = this.readVarInt();

        if (var2 > p_150789_1_ * 4) {
            throw new IOException("The received encoded string buffer length is longer than maximum allowed (" + var2 + " > " + p_150789_1_ * 4 + ")");
        } else if (var2 < 0) {
            throw new IOException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            final ByteBuf buf = this.buf.readBytes(var2);
            String var3 = new String(getBytes(buf, buf.readerIndex(), buf.readableBytes()), Charsets.UTF_8);

            if (var3.length() > p_150789_1_) {
                throw new IOException("The received string length is longer than maximum allowed (" + var2 + " > " + p_150789_1_ + ")");
            } else {
                return var3;
            }
        }
    }

    private static byte[] getBytes(ByteBuf buf, int start, int length) {
        int capacity = buf.capacity();
        if ((start | length | capacity | (start + length) | (capacity - (start + length))) < 0) {
            throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= start + length(" + length
                    + ") <= " + "buf.capacity(" + capacity + ')');
        }

        if (buf.hasArray()) {
            int baseOffset = buf.arrayOffset() + start;
            byte[] bytes = buf.array();
            return Arrays.copyOfRange(bytes, baseOffset, baseOffset + length);
        }

        byte[] bytes = new byte[length];
        buf.getBytes(start, bytes);
        return bytes;
    }

    public <T> void writeOptional(T obj, Consumer<T> consumer) {
        this.buf.writeBoolean(obj != null);
        if (obj != null) {
            consumer.accept(obj);
        }
    }

    public <T> T readOptional(Supplier<T> supplier) {
        boolean isPresent = this.buf.readBoolean();
        return isPresent ? supplier.get() : null;
    }

    public void writeString(String s) {
        byte[] arr = s.getBytes(Charsets.UTF_8);
        this.writeVarInt(arr.length);
        this.buf.writeBytes(arr);
    }

    public String readString() {
        int len = this.readVarInt();
        byte[] buffer = new byte[len];
        this.buf.readBytes(buffer);
        return new String(buffer, Charsets.UTF_8);
    }

    public void writeUUID(UUID uuid) {
        this.buf.writeLong(uuid.getMostSignificantBits());
        this.buf.writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUUID() {
        long mostSigBits = this.buf.readLong();
        long leastSigBits = this.buf.readLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public float readFloat() {
        return this.buf.readFloat();
    }

    public boolean readBool() {
        return this.buf.readBoolean();
    }

    public ByteBuf buf() {
        return this.buf;
    }
}
