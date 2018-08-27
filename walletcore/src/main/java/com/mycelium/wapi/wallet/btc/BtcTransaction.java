package com.mycelium.wapi.wallet.btc;

import com.mrd.bitlib.model.Transaction;
import com.mrd.bitlib.util.Sha256Hash;
import com.mycelium.wapi.wallet.GenericAddress;
import com.mycelium.wapi.wallet.GenericTransaction;
import com.mycelium.wapi.wallet.coins.BitcoinMain;
import com.mycelium.wapi.wallet.coins.CoinType;
import com.mycelium.wapi.wallet.coins.Value;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BtcTransaction implements GenericTransaction {
    final CoinType type;
    final Sha256Hash hash;
    final Transaction tx;
    final Value valueSent;
    final Value valueReceived;
    final Value value;
    private int timestamp;

    public GenericAddress getDestinationAddress() {
        return destinationAddress;
    }

    final GenericAddress destinationAddress;
    final ArrayList<GenericAddress> toAddresses;
    private int confirmations;
    final boolean isQueuedOutgoing;
    @Nullable
    final Value fee;

    public BtcTransaction(CoinType type, Transaction transaction,
                          long valueSent, long valueReceived, int timestamp,
                          GenericAddress destinationAddress, int confirmations, boolean isQueuedOutgoing,
                          ArrayList<GenericAddress> toAddresses,
                          @Nullable Value fee) {
        this.type = type;
        this.tx = transaction;
        this.hash = tx.getId();
        this.valueSent = Value.valueOf(type, valueSent);
        this.valueReceived = Value.valueOf(type, valueReceived);
        this.destinationAddress = destinationAddress;
        this.value = this.valueReceived.subtract(this.valueSent);
        this.timestamp = timestamp;
        this.confirmations = confirmations;
        this.isQueuedOutgoing = isQueuedOutgoing;
        this.toAddresses = toAddresses;
        this.fee = fee;
    }

    @Override
    public CoinType getType() {
        return type;
    }

    @Override
    public int getAppearedAtChainHeight() {
        return confirmations;
    }

    @Override
    public void setAppearedAtChainHeight(int appearedAtChainHeight) {
        this.confirmations = appearedAtChainHeight;
    }

    @Override
    public int getDepthInBlocks() {
        return 0;
    }

    @Override
    public void setDepthInBlocks(int depthInBlocks) {
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    @Nullable
    public Value getFee() {
        return fee;
    }

    @Override
    public List<GenericAddress> getReceivedFrom() {
        return toAddresses;
    }


    @Override
    public List<GenericOutput> getSentTo() {
        return null;
    }

    @Override
    public Value getSent() {
        return valueSent;
    }

    @Override
    public Value getReceived() {
        return valueReceived;
    }

    @Override
    public boolean isIncoming() {
        return value.value >= 0;
    }

    @Override
    public Sha256Hash getHash() {
        return tx.getHash();
    }

    @Override
    public String getHashAsString() {
        return getHash().toString();
    }

    @Override
    public byte[] getHashBytes() {
        return getHash().getBytes();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BtcTransaction other = (BtcTransaction) o;
        return getHash().equals(other.getHash());
    }

    public Transaction getRawTransaction() {
        return tx;
    }
}
