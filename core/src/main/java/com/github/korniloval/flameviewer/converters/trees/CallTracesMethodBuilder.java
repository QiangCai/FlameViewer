package com.github.korniloval.flameviewer.converters.trees;

import com.github.kornilova_l.flamegraph.proto.TreeProtos.Tree;
import org.jetbrains.annotations.NotNull;

import static com.github.korniloval.flameviewer.converters.trees.TreesUtilKt.*;
import static com.github.korniloval.flameviewer.server.handlers.CoreUtilKt.treeBuilder;

public class CallTracesMethodBuilder implements TreeBuilder {
    private final Tree tree;
    private final String className;
    private final String methodName;
    private final String desc;
    private Tree.Builder treeBuilder;
    private Tree.Node.Builder wantedMethodNode;
    private int maxDepth = 0;

    public CallTracesMethodBuilder(Tree sourceTree,
                                   String className,
                                   String methodName,
                                   String desc) {
        this.className = className;
        this.methodName = methodName;
        this.desc = desc;
        initTreeBuilder();
        traverseTreeAndFind(sourceTree.getBaseNode());
        setNodesOffsetRecursively(treeBuilder.getBaseNodeBuilder(), 0);
        setNodesIndices(treeBuilder.getBaseNodeBuilder());
        setTreeWidth(treeBuilder);
        setNodesCount(treeBuilder);
        setTimePercent(treeBuilder, sourceTree, wantedMethodNode);
        treeBuilder.setDepth(maxDepth);
        tree = treeBuilder.build();
    }

    /**
     * It calculates total time of method (not only self-time)
     */
    private static void setTimePercent(@NotNull Tree.Builder treeBuilder, @NotNull Tree sourceTree, @NotNull Tree.Node.Builder wantedMethodNode) {
        long timeOfMethod = calculateTimeOfMethodRecursively(sourceTree.getBaseNode(), wantedMethodNode);
        float timePercent = timeOfMethod / (float) sourceTree.getWidth();
        treeBuilder.getTreeInfoBuilder().setTimePercent(timePercent);
    }

    private static long calculateTimeOfMethodRecursively(@NotNull Tree.Node node, @NotNull Tree.Node.Builder wantedMethodNode) {
        if (isSameMethod(wantedMethodNode, node.getNodeInfo().getClassName(), node.getNodeInfo().getMethodName(),
                node.getNodeInfo().getDescription())) {
            /* do not go deeper. We do not want to add up time of recursive calls */
            return node.getWidth();
        }
        long time = 0;
        for (Tree.Node child : node.getNodesList()) {
            time += calculateTimeOfMethodRecursively(child, wantedMethodNode);
        }
        return time;
    }

    public Tree getTree() {
        return tree;
    }

    private void traverseTreeAndFind(Tree.Node node) {

        if (isSameMethod(wantedMethodNode, node.getNodeInfo().getClassName(), node.getNodeInfo().getMethodName(),
                node.getNodeInfo().getDescription())) {
            addNodesRecursively(treeBuilder.getBaseNodeBuilder(), node, 0);
        }
        for (Tree.Node childNode : node.getNodesList()) {
            traverseTreeAndFind(childNode);
        }
    }

    private void addNodesRecursively(Tree.Node.Builder nodeBuilder, // where to append child
                                     Tree.Node node, // from where get method and it's width
                                     int depth) {
        depth++;
        if (depth > maxDepth) {
            maxDepth = depth;
        }
        nodeBuilder = updateNodeList(nodeBuilder, node);
        for (Tree.Node childNode : node.getNodesList()) {
            addNodesRecursively(nodeBuilder, childNode, depth);
        }
    }

    private void initTreeBuilder() {
        Tree.Node.Builder baseNode = Tree.Node.newBuilder()
                .addNodes(Tree.Node.newBuilder()
                        .setNodeInfo(
                                createNodeInfo(
                                        className,
                                        methodName,
                                        desc
                                )
                        ));
        treeBuilder = treeBuilder(baseNode);
        wantedMethodNode = treeBuilder.getBaseNodeBuilder().getNodesBuilder(0);
    }
}
