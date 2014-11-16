import java.util.*;

public class CompliantNode implements Node {
    private double pGraph, pMalicious, pTxDistribution;
    private int numRounds, round;
    private ArrayList<Integer> followers, acceptedTxs;
    private HashMap<Integer, Boolean> trust;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.pGraph = p_graph;
        this.pMalicious = p_malicious;
        this.pTxDistribution = p_txDistribution;
        this.numRounds = numRounds;
        this.round = 0;
    }

    public void receiveCandidates(ArrayList<Integer[]> candidates) {
        HashMap<Integer, ArrayList<Integer>> byNode = new HashMap<Integer, ArrayList<Integer>>();

        for (Integer[] c : candidates) {
            int nodeId = c[0];
            int txId = c[1];

            if (!byNode.containsKey(nodeId)) {
                byNode.put(nodeId, new ArrayList<Integer>());
            }

            byNode.get(nodeId).add(txId);
        }

        ArrayList<Integer> acceptedTxs = new ArrayList<Integer>();

        for (Map.Entry<Integer, ArrayList<Integer>> e : byNode.entrySet()) {
            int node = e.getKey();
            ArrayList<Integer> txs = e.getValue();

            if (!isCompliant(txs)) {
                this.trust.put(node, false);
            }

            if (this.trust.get(node)) {
                acceptedTxs.addAll(txs);
            }
        }
        this.acceptedTxs = acceptedTxs;
        this.round += 1;
    }

    private boolean isCompliant(ArrayList<Integer> txs) {
        if ((this.round == 0 && !allOdd(txs))
            || (this.round == 1 && !allEven(txs))) {

            return false;
        }

        return true;
    }

    private boolean allOdd(ArrayList<Integer> a) {
        for (int n : a) {
            if (n % 2 == 0) return false;
        }
        return true;
    }

    private boolean allEven(ArrayList<Integer> a) {
        for (int n : a) {
            if (n % 2 != 0) return false;
        }
        return true;
    }

    private ArrayList<Transaction> getEvenTxs() {
        ArrayList<Transaction> ret = new ArrayList<Transaction>();
        for (int i : this.acceptedTxs) {
            if (i % 2 == 0) ret.add(new Transaction(i));
        }
        return ret;
    }

    private ArrayList<Transaction> getOddTxs() {
        ArrayList<Transaction> ret = new ArrayList<Transaction>();
        for (int i : this.acceptedTxs) {
            if (i % 2 != 0) ret.add(new Transaction(i));
        }
        return ret;
    }

    private ArrayList<Transaction> getAllTxs() {
        ArrayList<Transaction> ret = new ArrayList<Transaction>();
        for (int i : this.acceptedTxs) {
            ret.add(new Transaction(i));
        }
        return ret;
    }

    private Set<Transaction> getCompliantTxs() {
        Set<Transaction> txs = new HashSet<Transaction>();

        switch (this.round) {
            case 0: txs.addAll(this.getOddTxs());
                    break;
            case 1: txs.addAll(this.getEvenTxs());
                    break;
            default: txs.addAll(this.getAllTxs());
                    break;
        }

        return txs;
    }

    public Set<Transaction> getProposals() {
        return this.getCompliantTxs();
    }

    public void setFollowers(boolean[] followers) {
        ArrayList<Integer> follow = new ArrayList<Integer>();
        HashMap<Integer, Boolean> trust = new HashMap<Integer, Boolean>();

        for (int i = 0; i < followers.length; i++) {
            if (followers[i]) {
                follow.add(i);
                trust.put(i, true);
            }
        }

        this.followers = follow;
        this.trust = trust;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.acceptedTxs = new ArrayList<Integer>();

        for (Transaction tx : pendingTransactions) {
            this.acceptedTxs.add(tx.id);
        }
    }
}
