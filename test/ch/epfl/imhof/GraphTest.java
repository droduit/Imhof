package ch.epfl.imhof;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.*;
import ch.epfl.imhof.osm.OSMNode;
import static ch.epfl.imhof.TestUtil.*;

public class GraphTest {
	private static final int NODES_COUNT = 5; 

    @Test
    public void testGraph () {
		Graph.Builder<OSMNode> graphBuilder = new Graph.Builder<OSMNode>();

		OSMNode[] nodes = new OSMNode[NODES_COUNT];
		for (int i = 0; i < NODES_COUNT; i++) {
			nodes[i] = newOSMNode();
			graphBuilder.addNode(nodes[i]);
		}

		graphBuilder.addEdge(nodes[2], nodes[4]);
		for (int i = 0; i < NODES_COUNT; i++) {
			graphBuilder.addEdge(nodes[0], nodes[i]);

			if (i % 2 == 1)
				graphBuilder.addEdge(nodes[1], nodes[i]);

			if (i % 3 == 2)
				graphBuilder.addEdge(nodes[2], nodes[i]);

			if (i % 4 == 3)
				graphBuilder.addEdge(nodes[3], nodes[i]);
		}

		Graph<OSMNode> graph = graphBuilder.build();

		/* Checking nodes */
		assertFalse(graph.nodes().contains(newOSMNode()));
		for (OSMNode node : nodes)
			assertTrue(graph.nodes().contains(node));

		/* Checking edges */
		ArrayList<Set<OSMNode>> edges = new ArrayList<Set<OSMNode>>(NODES_COUNT);

		edges.add(new HashSet<OSMNode>());
		edges.get(0).add(nodes[0]);
		edges.get(0).add(nodes[1]);
		edges.get(0).add(nodes[2]);
		edges.get(0).add(nodes[3]);
		edges.get(0).add(nodes[4]);

		edges.add(new HashSet<OSMNode>());
		edges.get(1).add(nodes[0]);
		edges.get(1).add(nodes[1]);
		edges.get(1).add(nodes[3]);

		edges.add(new HashSet<OSMNode>());
		edges.get(2).add(nodes[0]);
		edges.get(2).add(nodes[2]);
		edges.get(2).add(nodes[4]);

		edges.add(new HashSet<OSMNode>());
		edges.get(3).add(nodes[0]);
		edges.get(3).add(nodes[1]);
		edges.get(3).add(nodes[3]);

		edges.add(new HashSet<OSMNode>());
		edges.get(4).add(nodes[0]);
		edges.get(4).add(nodes[2]);

		for (int i = 0; i < NODES_COUNT; i++) {
			assertEquals(edges.get(i), graph.neighborsOf(nodes[i]));
		}
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void crashTest1() {
        Graph.Builder<Integer> gb = new Graph.Builder<>();
        gb.addNode(1);
        gb.addEdge(2, 3);
    }
}
