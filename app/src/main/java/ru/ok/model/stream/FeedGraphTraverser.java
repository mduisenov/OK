package ru.ok.model.stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import ru.ok.model.stream.entities.BaseEntityBuilder;

public class FeedGraphTraverser {
    private final ArrayList<String> refsBuffer;
    private final Queue<String> refsQueue;
    private final Map<String, BaseEntityBuilder> resolvedEntities;
    private final Set<String> visitedNodes;

    public FeedGraphTraverser(Map<String, BaseEntityBuilder> resolvedEntities) {
        this.visitedNodes = new HashSet();
        this.refsQueue = new LinkedList();
        this.refsBuffer = new ArrayList();
        this.resolvedEntities = resolvedEntities;
    }

    public void traverse(String ref, FeedObject obj, FeedObjectVisitor visitor) {
        while (obj != null) {
            if (ref == null || !this.visitedNodes.contains(ref)) {
                obj.accept(ref, visitor);
                if (ref != null) {
                    this.visitedNodes.add(ref);
                }
                this.refsBuffer.clear();
                obj.getRefs(this.refsBuffer);
                Iterator i$ = this.refsBuffer.iterator();
                while (i$.hasNext()) {
                    this.refsQueue.add((String) i$.next());
                }
            }
            obj = null;
            while (!this.refsQueue.isEmpty()) {
                ref = (String) this.refsQueue.poll();
                obj = (FeedObject) this.resolvedEntities.get(ref);
                if (obj != null) {
                    break;
                }
            }
        }
        this.visitedNodes.clear();
        this.refsQueue.clear();
        this.refsBuffer.clear();
    }
}
