package com.chen1335.betterRaid.network;

import com.chen1335.betterRaid.BetterRaid;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public record SimpleDataSetter<T>(int id, StreamCodec<ByteBuf, T> codec, T value) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SimpleDataSetter<Object>> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BetterRaid.MODID, "simple_data_setter"));

    public static final StreamCodec<ByteBuf, SimpleDataSetter<Object>> STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> {
                ByteBufCodecs.VAR_INT.encode(buffer, value.id);
                value.codec.encode(buffer, value.value);
            },
            buffer -> {
                Integer v1 = ByteBufCodecs.VAR_INT.decode(buffer);
                DataSetterType<Object> dataSetterType = getDataSetter(v1);
                if (dataSetterType == null) {
                    throw new Error("DataSetterType of id: " + v1 + " is not exist");
                }
                StreamCodec<ByteBuf, Object> codec1 = dataSetterType.codec;
                Object value = codec1.decode(buffer);
                return new SimpleDataSetter<>(v1, codec1, value);
            }
    );

    private static int DataSetterId = 0;

    private static final ArrayList<DataSetterType<Object>> dates = new ArrayList<>();

    public static DataSetterType<Object> getDataSetter(int id) {
        if (id <= DataSetterId) {
            return dates.get(id);
        }
        return null;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void clientHandler(IPayloadContext iPayloadContext) {
        Objects.requireNonNull(getDataSetter(id)).consumer.accept(value);
    }

    public record DataSetterType<T2>(int id, StreamCodec<ByteBuf, T2> codec, Consumer<T2> consumer) {

        public static <T3> DataSetterType<T3> create(StreamCodec<ByteBuf, T3> codec, Consumer<T3> consumer) {
            DataSetterType<T3> dataSetterType = new DataSetterType<T3>(DataSetterId, codec, consumer);
            dates.add((DataSetterType<Object>) dataSetterType);
            DataSetterId++;
            return dataSetterType;
        }

        public void send(ServerPlayer serverPlayer, T2 vale) {
            PacketDistributor.sendToPlayer(serverPlayer, new SimpleDataSetter<>(id, codec, vale));
        }
    }
}
