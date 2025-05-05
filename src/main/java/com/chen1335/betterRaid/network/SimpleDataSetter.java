package com.chen1335.betterRaid.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleDataSetter<T> {
    private final int id;
    private final T value;

    private static int DataSetterId = 0;
    private static final ArrayList<DataSetterType<?>> dates = new ArrayList<>();

    public SimpleDataSetter(int id, T value) {
        this.id = id;
        this.value = value;
    }

    public static void encode(SimpleDataSetter<?> message, FriendlyByteBuf buffer) {
        buffer.writeVarInt(message.id);
        DataSetterType<?> dataSetterType = getDataSetter(message.id);
        if (dataSetterType != null) {
            dataSetterType.encode(buffer, message.value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> SimpleDataSetter<T> decode(FriendlyByteBuf buffer) {
        int id = buffer.readVarInt();
        DataSetterType<T> dataSetterType = getDataSetter(id);
        if (dataSetterType == null) {
            throw new Error("DataSetterType of id: " + id + " is not exist");
        }
        T value = dataSetterType.decode(buffer);
        return new SimpleDataSetter<>(id, value);
    }

    public static void handle(SimpleDataSetter<?> message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() == null && context.getDirection().getReceptionSide().isClient()) {
                DataSetterType<?> dataSetterType = getDataSetter(message.id);
                if (dataSetterType != null) {
                    dataSetterType.accept(message.value);
                }
            }
        });
        context.setPacketHandled(true);
    }

    @SuppressWarnings("unchecked")
    private static <T> DataSetterType<T> getDataSetter(int id) {
        if (id <= DataSetterId && id >= 0 && id < dates.size()) {
            return (DataSetterType<T>) dates.get(id);
        }
        return null;
    }

    public static class DataSetterType<T> {
        private final int id;
        private final Consumer<T> consumer;
        private final DataEncoder<T> encoder;
        private final DataDecoder<T> decoder;

        private DataSetterType(int id, Consumer<T> consumer, DataEncoder<T> encoder, DataDecoder<T> decoder) {
            this.id = id;
            this.consumer = consumer;
            this.encoder = encoder;
            this.decoder = decoder;
        }

        public static DataSetterType<Integer> create(Consumer<Integer> consumer) {
            return create(consumer,
                    FriendlyByteBuf::writeVarInt,
                    FriendlyByteBuf::readVarInt);
        }

        public static <T> DataSetterType<T> create(Consumer<T> consumer, DataEncoder<T> encoder, DataDecoder<T> decoder) {
            DataSetterType<T> dataSetterType = new DataSetterType<>(DataSetterId, consumer, encoder, decoder);
            dates.add(dataSetterType);
            DataSetterId++;
            return dataSetterType;
        }

        public void send(ServerPlayer serverPlayer, T value) {
            Payloads.sendToPlayer(serverPlayer, new SimpleDataSetter<>(id, value));
        }

        @SuppressWarnings("unchecked")
        public void accept(Object value) {
            consumer.accept((T) value);
        }

        @SuppressWarnings("unchecked")
        public void encode(FriendlyByteBuf buffer, Object value) {
            encoder.encode(buffer, (T) value);
        }

        public T decode(FriendlyByteBuf buffer) {
            return decoder.decode(buffer);
        }
    }

    public interface DataEncoder<T> {
        void encode(FriendlyByteBuf buffer, T value);
    }

    public interface DataDecoder<T> {
        T decode(FriendlyByteBuf buffer);
    }
}