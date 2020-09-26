package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.IteratorWithOrigin;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ian
 */
public class GCodeEventNodeTest
{

    public GCodeEventNodeTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of isLeaf method, of class GCodeEventNode.
     */
    @Test
    public void testIsLeaf()
    {
        System.out.println("isLeaf");
        GCodeEventNode instance = new GCodeEventNodeTestImpl();
        assertEquals(true, instance.isLeaf());

        instance.addChildAtEnd(new GCodeEventNodeTestImpl());
        assertEquals(false, instance.isLeaf());
    }

//    /**
//     * Test of stream method, of class GCodeEventNode.
//     */
//    @Test
//    public void testStream()
//    {
//        System.out.println("stream");
//        GCodeEventNode node1 = new GCodeEventNodeTestImpl();
//        GCodeEventNode node1_1 = new GCodeEventNodeTestImpl();
//        GCodeEventNode node1_2 = new GCodeEventNodeTestImpl();
//        GCodeEventNode node1_3 = new GCodeEventNodeTestImpl();
//        GCodeEventNode node1_3_1 = new GCodeEventNodeTestImpl();
//
//        node1.addChild(0, node1_3);
//        node1_3.addChild(0, node1_3_1);
//        node1.addChild(0, node1_2);
//        node1.addChild(0, node1_1);
//
//        //Should yied a stream of:
//        //node1_2, node1_3, node1_3_1
//        Stream<GCodeEventNode> result = node1.stream();
//
//        ArrayList<GCodeEventNode> resultList = new ArrayList<>();
//        result.forEach(node -> resultList.add(node));
//        assertEquals(5, resultList.size());
//        assertSame(node1, resultList.get(0));
//        assertSame(node1_1, resultList.get(1));
//        assertSame(node1_2, resultList.get(2));
//        assertSame(node1_3, resultList.get(3));
//        assertSame(node1_3_1, resultList.get(4));
//    }
//
//    /**
//     * Test of stream method, of class GCodeEventNode.
//     */
//    @Test
//    public void testStreamChildrenAndMe()
//    {
//        System.out.println("streamChildrenAndMe");
//        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
//        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
//        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
//        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
//        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
//        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
//        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
//        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
//        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
//        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
//        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
//        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
//        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
//        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
//        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
//        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");
//
//        nodeB1.addChild(0, nodeC1);
//        nodeB1.addChild(1, nodeC2);
//        nodeB1.addChild(2, nodeC3);
//
//        nodeB2.addChild(0, nodeC4);
//        nodeB2.addChild(1, nodeC5);
//        nodeB2.addChild(2, nodeC6);
//
//        nodeB3.addChild(0, nodeC7);
//        nodeB3.addChild(1, nodeC8);
//        nodeB3.addChild(2, nodeC9);
//
//        nodeC7.addChild(0, nodeD1);
//        nodeC7.addChild(1, nodeD2);
//        nodeC7.addChild(2, nodeD3);
//
//        nodeA.addChild(0, nodeB1);
//        nodeA.addChild(1, nodeB2);
//        nodeA.addChild(2, nodeB3);
//
//        // From C8 should be C8
//        Stream<GCodeEventNode> result1 = nodeC8.streamChildrenAndMe();
//
//        ArrayList<GCodeEventNode> resultList1 = new ArrayList<>();
//        result1.forEach(node -> resultList1.add(node));
//
//        assertEquals(1, resultList1.size());
//        assertSame(nodeC8, resultList1.get(0));
//
//        // From C7 should be C7, D1, D2, D3
//        Stream<GCodeEventNode> result2 = nodeC7.streamChildrenAndMe();
//
//        ArrayList<GCodeEventNode> resultList2 = new ArrayList<>();
//        result2.forEach(node -> resultList2.add(node));
//
//        assertEquals(4, resultList2.size());
//        assertSame(nodeC7, resultList2.get(0));
//        assertSame(nodeD1, resultList2.get(1));
//        assertSame(nodeD2, resultList2.get(2));
//        assertSame(nodeD3, resultList2.get(3));
//
//        // From B3 should be B3,C7,D1,D2,D3,C8,C9
//        Stream<GCodeEventNode> result3 = nodeB3.streamChildrenAndMe();
//
//        ArrayList<GCodeEventNode> resultList3 = new ArrayList<>();
//        result3.forEach(node -> resultList3.add(node));
//
//        assertEquals(7, resultList3.size());
//        assertSame(nodeB3, resultList3.get(0));
//        assertSame(nodeC7, resultList3.get(1));
//        assertSame(nodeD1, resultList3.get(2));
//        assertSame(nodeD2, resultList3.get(3));
//        assertSame(nodeD3, resultList3.get(4));
//        assertSame(nodeC8, resultList3.get(5));
//        assertSame(nodeC9, resultList3.get(6));
//    }
//
    @Test
    public void testChildrenAndMeBackwardsIterator()
    {
        System.out.println("streamChildrenBackwards");
        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        // From C8 should be C8
        IteratorWithOrigin<GCodeEventNode> nodeC8Iterator = nodeC8.childrenAndMeBackwardsIterator();
        ArrayList<GCodeEventNode> resultList1 = new ArrayList<>();

        while (nodeC8Iterator.hasNext())
        {
            resultList1.add(nodeC8Iterator.next());
        }

        assertEquals(1, resultList1.size());
        assertSame(nodeC8, resultList1.get(0));

        // From C7 should be D3,D2,D1,C7
        IteratorWithOrigin<GCodeEventNode> nodeC7Iterator = nodeC7.childrenAndMeBackwardsIterator();

        ArrayList<GCodeEventNode> resultList2 = new ArrayList<>();

        while (nodeC7Iterator.hasNext())
        {
            resultList2.add(nodeC7Iterator.next());
        }

        assertEquals(4, resultList2.size());
        assertSame(nodeD3, resultList2.get(0));
        assertSame(nodeD2, resultList2.get(1));
        assertSame(nodeD1, resultList2.get(2));
        assertSame(nodeC7, resultList2.get(3));

        // From B3 should be C9,C8,D3,D2,D1,C7,B3
        IteratorWithOrigin<GCodeEventNode> nodeB3Iterator = nodeB3.childrenAndMeBackwardsIterator();

        ArrayList<GCodeEventNode> resultList3 = new ArrayList<>();
        while (nodeB3Iterator.hasNext())
        {
            GCodeEventNode node = nodeB3Iterator.next();
            resultList3.add(node);
        }

        assertEquals(7, resultList3.size());
        assertSame(nodeC9, resultList3.get(0));
        assertSame(nodeC8, resultList3.get(1));
        assertSame(nodeD3, resultList3.get(2));
        assertSame(nodeD2, resultList3.get(3));
        assertSame(nodeD1, resultList3.get(4));
        assertSame(nodeC7, resultList3.get(5));
        assertSame(nodeB3, resultList3.get(6));
    }

    @Test
    public void testMeAndSiblingsBackwardsIterator()
    {
        System.out.println("streamChildrenBackwards");
        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        // From C8 should be C8 and C7
        IteratorWithOrigin<GCodeEventNode> nodeC8Iterator = nodeC8.meAndSiblingsBackwardsIterator();
        ArrayList<GCodeEventNode> resultList1 = new ArrayList<>();

        while (nodeC8Iterator.hasNext())
        {
            resultList1.add(nodeC8Iterator.next());
        }

        assertEquals(2, resultList1.size());
        assertSame(nodeC8, resultList1.get(0));
        assertSame(nodeC7, resultList1.get(1));

        // From D3 should be D3,D2,D1
        IteratorWithOrigin<GCodeEventNode> nodeD3Iterator = nodeD3.meAndSiblingsBackwardsIterator();

        ArrayList<GCodeEventNode> resultList2 = new ArrayList<>();

        while (nodeD3Iterator.hasNext())
        {
            resultList2.add(nodeD3Iterator.next());
        }

        assertEquals(3, resultList2.size());
        assertSame(nodeD3, resultList2.get(0));
        assertSame(nodeD2, resultList2.get(1));
        assertSame(nodeD1, resultList2.get(2));
    }

    @Test
    public void testSiblingsBackwardsIterator()
    {
        System.out.println("SiblingsBackwardsIterator");
        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        // From C8 should be C7
        IteratorWithOrigin<GCodeEventNode> nodeC8Iterator = nodeC8.siblingsBackwardsIterator();
        ArrayList<GCodeEventNode> resultList1 = new ArrayList<>();

        while (nodeC8Iterator.hasNext())
        {
            resultList1.add(nodeC8Iterator.next());
        }

        assertEquals(1, resultList1.size());
        assertSame(nodeC7, resultList1.get(0));

        // From D3 should be D2,D1
        IteratorWithOrigin<GCodeEventNode> nodeD3Iterator = nodeD3.siblingsBackwardsIterator();

        ArrayList<GCodeEventNode> resultList2 = new ArrayList<>();

        while (nodeD3Iterator.hasNext())
        {
            resultList2.add(nodeD3Iterator.next());
        }

        assertEquals(2, resultList2.size());
        assertSame(nodeD2, resultList2.get(0));
        assertSame(nodeD1, resultList2.get(1));
    }

    @Test
    public void testSiblingsIterator()
    {
        System.out.println("SiblingsIterator");
        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        // From C8 should be C9
        IteratorWithOrigin<GCodeEventNode> nodeC8Iterator = nodeC8.siblingsIterator();
        ArrayList<GCodeEventNode> resultList1 = new ArrayList<>();

        while (nodeC8Iterator.hasNext())
        {
            resultList1.add(nodeC8Iterator.next());
        }

        assertEquals(1, resultList1.size());
        assertSame(nodeC9, resultList1.get(0));

        // From D2 should be D3
        IteratorWithOrigin<GCodeEventNode> nodeD2Iterator = nodeD2.siblingsIterator();

        ArrayList<GCodeEventNode> resultList2 = new ArrayList<>();

        while (nodeD2Iterator.hasNext())
        {
            resultList2.add(nodeD2Iterator.next());
        }

        assertEquals(1, resultList2.size());
        assertSame(nodeD3, resultList2.get(0));

        // From D3 should be nothing
        IteratorWithOrigin<GCodeEventNode> nodeD3Iterator = nodeD3.siblingsIterator();

        ArrayList<GCodeEventNode> resultList3 = new ArrayList<>();

        while (nodeD3Iterator.hasNext())
        {
            resultList3.add(nodeD3Iterator.next());
        }

        assertEquals(0, resultList3.size());
    }

    @Test
    public void testMeAndSiblingsIterator()
    {
        System.out.println("SiblingsIterator");
        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        // From C8 should be C8, C9
        IteratorWithOrigin<GCodeEventNode> nodeC8Iterator = nodeC8.meAndSiblingsIterator();
        ArrayList<GCodeEventNode> resultList1 = new ArrayList<>();

        while (nodeC8Iterator.hasNext())
        {
            resultList1.add(nodeC8Iterator.next());
        }

        assertEquals(2, resultList1.size());
        assertSame(nodeC8, resultList1.get(0));
        assertSame(nodeC9, resultList1.get(1));

        // From D2 should be D2, D3
        IteratorWithOrigin<GCodeEventNode> nodeD2Iterator = nodeD2.meAndSiblingsIterator();

        ArrayList<GCodeEventNode> resultList2 = new ArrayList<>();

        while (nodeD2Iterator.hasNext())
        {
            resultList2.add(nodeD2Iterator.next());
        }

        assertEquals(2, resultList2.size());
        assertSame(nodeD2, resultList2.get(0));
        assertSame(nodeD3, resultList2.get(1));

        // From D3 should be D3
        IteratorWithOrigin<GCodeEventNode> nodeD3Iterator = nodeD3.meAndSiblingsIterator();

        ArrayList<GCodeEventNode> resultList3 = new ArrayList<>();

        while (nodeD3Iterator.hasNext())
        {
            resultList3.add(nodeD3Iterator.next());
        }

        assertEquals(1, resultList3.size());
        assertSame(nodeD3, resultList3.get(0));
    }

    @Test
    public void testTreeSpanningIterator()
    {
        System.out.println("treeSpanningIterator");
        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        // From C7 should be D1, D2, D3
        Iterator<GCodeEventNode> nodeC7Iterator = nodeC7.treeSpanningIterator(null);
        ArrayList<GCodeEventNode> resultList1 = new ArrayList<>();

        while (nodeC7Iterator.hasNext())
        {
            resultList1.add(nodeC7Iterator.next());
        }

        assertEquals(3, resultList1.size());
        assertSame(nodeD1, resultList1.get(0));
        assertSame(nodeD2, resultList1.get(1));
        assertSame(nodeD3, resultList1.get(2));

        // From B3 should be C7, D1, D2, D3, C8, C9
        Iterator<GCodeEventNode> nodeB3Iterator = nodeB3.treeSpanningIterator(null);

        ArrayList<GCodeEventNode> resultList2 = new ArrayList<>();

        while (nodeB3Iterator.hasNext())
        {
            resultList2.add(nodeB3Iterator.next());
        }

        assertEquals(6, resultList2.size());
        assertSame(nodeC7, resultList2.get(0));
        assertSame(nodeD1, resultList2.get(1));
        assertSame(nodeD2, resultList2.get(2));
        assertSame(nodeD3, resultList2.get(3));
        assertSame(nodeC8, resultList2.get(4));
        assertSame(nodeC9, resultList2.get(5));

        // From A should be B1, C1, C2, C3, B2, C4, C5, C6, B3, C7, D1, D2, D3, C8, C9
        Iterator<GCodeEventNode> nodeAIterator = nodeA.treeSpanningIterator(null);

        ArrayList<GCodeEventNode> resultList3 = new ArrayList<>();

        while (nodeAIterator.hasNext())
        {
            resultList3.add(nodeAIterator.next());
        }

        assertEquals(15, resultList3.size());
        assertSame(nodeB1, resultList3.get(0));
        assertSame(nodeC1, resultList3.get(1));
        assertSame(nodeC2, resultList3.get(2));
        assertSame(nodeC3, resultList3.get(3));
        assertSame(nodeB2, resultList3.get(4));
        assertSame(nodeC4, resultList3.get(5));
        assertSame(nodeC5, resultList3.get(6));
        assertSame(nodeC6, resultList3.get(7));
        assertSame(nodeB3, resultList3.get(8));
        assertSame(nodeC7, resultList3.get(9));
        assertSame(nodeD1, resultList3.get(10));
        assertSame(nodeD2, resultList3.get(11));
        assertSame(nodeD3, resultList3.get(12));
        assertSame(nodeC8, resultList3.get(13));
        assertSame(nodeC9, resultList3.get(14));

        // From C9 should be nothing
        Iterator<GCodeEventNode> nodeC9Iterator = nodeC9.treeSpanningIterator(null);

        ArrayList<GCodeEventNode> resultList4 = new ArrayList<>();

        while (nodeC9Iterator.hasNext())
        {
            resultList4.add(nodeC9Iterator.next());
        }

        assertEquals(0, resultList4.size());

        // From A starting from B3,C7 should be D1, D2, D3, C8, C9
        List<GCodeEventNode> startList1 = new ArrayList();
        startList1.add(nodeB3);
        startList1.add(nodeC7);

        Iterator<GCodeEventNode> nodeA_B3C7Iterator = nodeA.treeSpanningIterator(startList1);

        ArrayList<GCodeEventNode> resultList5 = new ArrayList<>();

        while (nodeA_B3C7Iterator.hasNext())
        {
            resultList5.add(nodeA_B3C7Iterator.next());
        }

        assertEquals(5, resultList5.size());
        assertSame(nodeD1, resultList5.get(0));
        assertSame(nodeD2, resultList5.get(1));
        assertSame(nodeD3, resultList5.get(2));
        assertSame(nodeC8, resultList5.get(3));
        assertSame(nodeC9, resultList5.get(4));
    }

    @Test
    public void testTreeSpanningBackwardsIterator()
    {
        System.out.println("treeSpanningBackwardsIterator");
        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        // From C8 should be D3, D2, D1, C7, B3, C6, C5, C4, B2, C3, C2, C1, B1, A
        Iterator<GCodeEventNode> nodeC8Iterator = nodeC8.treeSpanningBackwardsIterator();
        ArrayList<GCodeEventNode> resultList1 = new ArrayList<>();

        while (nodeC8Iterator.hasNext())
        {
            resultList1.add(nodeC8Iterator.next());
        }

        assertEquals(14, resultList1.size());
        assertSame(nodeD3, resultList1.get(0));
        assertSame(nodeD2, resultList1.get(1));
        assertSame(nodeD1, resultList1.get(2));
        assertSame(nodeC7, resultList1.get(3));
        assertSame(nodeB3, resultList1.get(4));
        assertSame(nodeC6, resultList1.get(5));
        assertSame(nodeC5, resultList1.get(6));
        assertSame(nodeC4, resultList1.get(7));
        assertSame(nodeB2, resultList1.get(8));
        assertSame(nodeC3, resultList1.get(9));
        assertSame(nodeC2, resultList1.get(10));
        assertSame(nodeC1, resultList1.get(11));
        assertSame(nodeB1, resultList1.get(12));
        assertSame(nodeA, resultList1.get(13));

        // From C7 should be B3, C6, C5, C4, B2, C3, C2, C1, B1, A
        Iterator<GCodeEventNode> nodeC7Iterator = nodeC7.treeSpanningBackwardsIterator();
        ArrayList<GCodeEventNode> resultList2 = new ArrayList<>();

        while (nodeC7Iterator.hasNext())
        {
            resultList2.add(nodeC7Iterator.next());
        }

        assertEquals(10, resultList2.size());
        assertSame(nodeB3, resultList2.get(0));
        assertSame(nodeC6, resultList2.get(1));
        assertSame(nodeC5, resultList2.get(2));
        assertSame(nodeC4, resultList2.get(3));
        assertSame(nodeB2, resultList2.get(4));
        assertSame(nodeC3, resultList2.get(5));
        assertSame(nodeC2, resultList2.get(6));
        assertSame(nodeC1, resultList2.get(7));
        assertSame(nodeB1, resultList2.get(8));
        assertSame(nodeA, resultList2.get(9));

        // From A starting from B2,C6 should be C5, C4, C3, B2, C3, C2, C1, B2, A
//        List<GCodeEventNode> startList1 = new ArrayList();
//        startList1.add(nodeB2);
//        startList1.add(nodeC6);
//
//        Iterator<GCodeEventNode> nodeA_B3C7Iterator = nodeA.treeSpanningBackwardsIterator(startList1);
//
//        ArrayList<GCodeEventNode> resultList5 = new ArrayList<>();
//
//        while (nodeA_B3C7Iterator.hasNext())
//        {
//            resultList5.add(nodeA_B3C7Iterator.next());
//        }
//
//        assertEquals(5, resultList5.size());
//        assertSame(nodeD1, resultList5.get(0));
//        assertSame(nodeD2, resultList5.get(1));
//        assertSame(nodeD3, resultList5.get(2));
//        assertSame(nodeC8, resultList5.get(3));
//        assertSame(nodeC9, resultList5.get(4));
//        // From B3 should be C7, D1, D2, D3, C8, C9
//        Iterator<GCodeEventNode> nodeB3Iterator = nodeB3.treeSpanningIterator();
//
//        ArrayList<GCodeEventNode> resultList2 = new ArrayList<>();
//
//        while (nodeB3Iterator.hasNext())
//        {
//            resultList2.add(nodeB3Iterator.next());
//        }
//
//        assertEquals(6, resultList2.size());
//        assertSame(nodeC7, resultList2.get(0));
//        assertSame(nodeD1, resultList2.get(1));
//        assertSame(nodeD2, resultList2.get(2));
//        assertSame(nodeD3, resultList2.get(3));
//        assertSame(nodeC8, resultList2.get(4));
//        assertSame(nodeC9, resultList2.get(5));
//
//        // From A should be B1, C1, C2, C3, B2, C4, C5, C6, B3, C7, D1, D2, D3, C8, C9
//        Iterator<GCodeEventNode> nodeAIterator = nodeA.treeSpanningIterator();
//
//        ArrayList<GCodeEventNode> resultList3 = new ArrayList<>();
//
//        while (nodeAIterator.hasNext())
//        {
//            resultList3.add(nodeAIterator.next());
//        }
//
//        assertEquals(15, resultList3.size());
//        assertSame(nodeB1, resultList3.get(0));
//        assertSame(nodeC1, resultList3.get(1));
//        assertSame(nodeC2, resultList3.get(2));
//        assertSame(nodeC3, resultList3.get(3));
//        assertSame(nodeB2, resultList3.get(4));
//        assertSame(nodeC4, resultList3.get(5));
//        assertSame(nodeC5, resultList3.get(6));
//        assertSame(nodeC6, resultList3.get(7));
//        assertSame(nodeB3, resultList3.get(8));
//        assertSame(nodeC7, resultList3.get(9));
//        assertSame(nodeD1, resultList3.get(10));
//        assertSame(nodeD2, resultList3.get(11));
//        assertSame(nodeD3, resultList3.get(12));
//        assertSame(nodeC8, resultList3.get(13));
//        assertSame(nodeC9, resultList3.get(14));
//
//        // From C9 should be nothing
//        IteratorWithOrigin<GCodeEventNode> nodeC9Iterator = nodeC9.siblingsIterator();
//
//        ArrayList<GCodeEventNode> resultList4 = new ArrayList<>();
//
//        while (nodeC9Iterator.hasNext())
//        {
//            resultList4.add(nodeC9Iterator.next());
//        }
//
//        assertEquals(0, resultList4.size());
    }
    
    @Test
    public void testTreeSpanningBackwardsAndMeIterator()
    {
        System.out.println("treeSpanningBackwardsAndMeIterator");
        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        // From C8 should be C8, D3, D2, D1, C7, B3, C6, C5, C4, B2, C3, C2, C1, B1, A
        Iterator<GCodeEventNode> nodeC8Iterator = nodeC8.treeSpanningBackwardsAndMeIterator();
        ArrayList<GCodeEventNode> resultList1 = new ArrayList<>();

        while (nodeC8Iterator.hasNext())
        {
            resultList1.add(nodeC8Iterator.next());
        }

        assertEquals(15, resultList1.size());
        assertSame(nodeC8, resultList1.get(0));
        assertSame(nodeD3, resultList1.get(1));
        assertSame(nodeD2, resultList1.get(2));
        assertSame(nodeD1, resultList1.get(3));
        assertSame(nodeC7, resultList1.get(4));
        assertSame(nodeB3, resultList1.get(5));
        assertSame(nodeC6, resultList1.get(6));
        assertSame(nodeC5, resultList1.get(7));
        assertSame(nodeC4, resultList1.get(8));
        assertSame(nodeB2, resultList1.get(9));
        assertSame(nodeC3, resultList1.get(10));
        assertSame(nodeC2, resultList1.get(11));
        assertSame(nodeC1, resultList1.get(12));
        assertSame(nodeB1, resultList1.get(13));
        assertSame(nodeA, resultList1.get(14));

        // From C7 should be C7, B3, C6, C5, C4, B2, C3, C2, C1, B1, A
        Iterator<GCodeEventNode> nodeC7Iterator = nodeC7.treeSpanningBackwardsAndMeIterator();
        ArrayList<GCodeEventNode> resultList2 = new ArrayList<>();

        while (nodeC7Iterator.hasNext())
        {
            resultList2.add(nodeC7Iterator.next());
        }

        assertEquals(11, resultList2.size());
        assertSame(nodeC7, resultList2.get(0));
        assertSame(nodeB3, resultList2.get(1));
        assertSame(nodeC6, resultList2.get(2));
        assertSame(nodeC5, resultList2.get(3));
        assertSame(nodeC4, resultList2.get(4));
        assertSame(nodeB2, resultList2.get(5));
        assertSame(nodeC3, resultList2.get(6));
        assertSame(nodeC2, resultList2.get(7));
        assertSame(nodeC1, resultList2.get(8));
        assertSame(nodeB1, resultList2.get(9));
        assertSame(nodeA, resultList2.get(10));
    }
//
//    /**
//     * Test of streamFromHere method, of class GCodeEventNode.
//     */
//    @Test
//    public void testStreamFromHere()
//    {
//        System.out.println("streamFromHere");
//        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
//        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
//        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
//        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
//        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
//        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
//        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
//        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
//        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
//        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
//        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
//        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
//        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
//
//        nodeB1.addChild(0, nodeC3);
//        nodeB1.addChild(0, nodeC2);
//        nodeB1.addChild(0, nodeC1);
//
//        nodeB2.addChild(0, nodeC6);
//        nodeB2.addChild(0, nodeC5);
//        nodeB2.addChild(0, nodeC4);
//
//        nodeB3.addChild(0, nodeC9);
//        nodeB3.addChild(0, nodeC8);
//        nodeB3.addChild(0, nodeC7);
//
//        nodeA.addChild(0, nodeB3);
//        nodeA.addChild(0, nodeB2);
//        nodeA.addChild(0, nodeB1);
//
//        //Stream from here node C5 should yield a stream of:
//        //C5, C6, B3, C7, C8, C9
//        try
//        {
//            Stream<GCodeEventNode> result = nodeC5.streamFromHere();
//
//            ArrayList<GCodeEventNode> resultList = new ArrayList<>();
//            result.forEach(node ->
//            {
//                System.out.println("Adding node " + node.toString());
//                resultList.add(node);
//            });
//
//            assertEquals(6, resultList.size());
//            assertSame(nodeC5, resultList.get(0));
//            assertSame(nodeC6, resultList.get(1));
//            assertSame(nodeB3, resultList.get(2));
//            assertSame(nodeC7, resultList.get(3));
//            assertSame(nodeC8, resultList.get(4));
//            assertSame(nodeC9, resultList.get(5));
//        } catch (NodeProcessingException ex)
//        {
//            fail("Node processing exception");
//        }
//    }
//
//    /**
//     * Test of streamBackwardsFromHere method, of class GCodeEventNode.
//     */
//    @Test
//    public void testStreamBackwardsFromHere()
//    {
//        System.out.println("streamBackwardsFromHere");
//        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
//        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
//        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
//        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
//        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
//        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
//        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
//        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
//        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
//        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
//        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
//        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
//        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
//        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
//        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
//        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");
//
//        nodeB1.addChild(0, nodeC3);
//        nodeB1.addChild(0, nodeC2);
//        nodeB1.addChild(0, nodeC1);
//
//        nodeB2.addChild(0, nodeC6);
//        nodeB2.addChild(0, nodeC5);
//        nodeB2.addChild(0, nodeC4);
//
//        nodeB3.addChild(0, nodeC9);
//        nodeB3.addChild(0, nodeC8);
//        nodeB3.addChild(0, nodeC7);
//
//        nodeC7.addChild(0, nodeD3);
//        nodeC7.addChild(0, nodeD2);
//        nodeC7.addChild(0, nodeD1);
//
//        nodeA.addChild(0, nodeB3);
//        nodeA.addChild(0, nodeB2);
//        nodeA.addChild(0, nodeB1);
//
//        //Stream backwards from node C8 should yield a stream of:
//        //C8,D3,D2,D1,C7,B3,C6,C5,C4,B2,C3,C2,C1,B1,A
//        try
//        {
//            Stream<GCodeEventNode> result = nodeC8.streamBackwardsFromHere();
//
//            ArrayList<GCodeEventNode> resultList = new ArrayList<>();
//            result.forEach(node ->
//            {
//                System.out.println("Adding node " + node.toString());
//                resultList.add(node);
//            });
//
//            assertEquals(15, resultList.size());
//            assertSame(nodeC8, resultList.get(0));
//            assertSame(nodeD3, resultList.get(1));
//            assertSame(nodeD2, resultList.get(2));
//            assertSame(nodeD1, resultList.get(3));
//            assertSame(nodeC7, resultList.get(4));
//            assertSame(nodeB3, resultList.get(5));
//            assertSame(nodeC6, resultList.get(6));
//            assertSame(nodeC5, resultList.get(7));
//            assertSame(nodeC4, resultList.get(8));
//            assertSame(nodeB2, resultList.get(9));
//            assertSame(nodeC3, resultList.get(10));
//            assertSame(nodeC2, resultList.get(11));
//            assertSame(nodeC1, resultList.get(12));
//            assertSame(nodeB1, resultList.get(13));
//            assertSame(nodeA, resultList.get(14));
//        } catch (NodeProcessingException ex)
//        {
//            fail("Node processing exception");
//        }
//    }
//
//    /**
//     * Test of streamSiblingsFromHere method, of class GCodeEventNode.
//     */
//    @Test
//    public void testStreamSiblingsFromHere()
//    {
//        System.out.println("streamSiblingsFromHere");
//        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
//        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
//        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
//        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
//        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
//        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
//        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
//        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
//        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
//        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
//        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
//        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
//        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
//
//        nodeB1.addChild(0, nodeC3);
//        nodeB1.addChild(0, nodeC2);
//        nodeB1.addChild(0, nodeC1);
//
//        nodeB2.addChild(0, nodeC6);
//        nodeB2.addChild(0, nodeC5);
//        nodeB2.addChild(0, nodeC4);
//
//        nodeB3.addChild(0, nodeC9);
//        nodeB3.addChild(0, nodeC8);
//        nodeB3.addChild(0, nodeC7);
//
//        nodeA.addChild(0, nodeB3);
//        nodeA.addChild(0, nodeB2);
//        nodeA.addChild(0, nodeB1);
//
//        //Stream from here node C5 should yield a stream of:
//        //C6
//        try
//        {
//            Stream<GCodeEventNode> result = nodeC5.streamSiblingsFromHere();
//
//            ArrayList<GCodeEventNode> resultList = new ArrayList<>();
//            result.forEach(node ->
//            {
//                System.out.println("Adding node " + node.toString());
//                resultList.add(node);
//            });
//
//            assertEquals(1, resultList.size());
//            assertSame(nodeC6, resultList.get(0));
//        } catch (NodeProcessingException ex)
//        {
//            fail("Node processing exception");
//        }
//    }
//
//    /**
//     * Test of streamSiblingsBackwardsFromHere method, of class GCodeEventNode.
//     */
//    @Test
//    public void testStreamSiblingsBackwardsFromHere()
//    {
//        System.out.println("streamSiblingsBackwardsFromHere");
//        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
//        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
//        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
//        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
//        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
//        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
//        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
//        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
//        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
//        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
//        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
//        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
//        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
//
//        nodeB1.addChild(0, nodeC3);
//        nodeB1.addChild(0, nodeC2);
//        nodeB1.addChild(0, nodeC1);
//
//        nodeB2.addChild(0, nodeC6);
//        nodeB2.addChild(0, nodeC5);
//        nodeB2.addChild(0, nodeC4);
//
//        nodeB3.addChild(0, nodeC7);
//        nodeB3.addChild(1, nodeC8);
//        nodeB3.addChild(2, nodeC9);
//
//        nodeA.addChild(0, nodeB3);
//        nodeA.addChild(0, nodeB2);
//        nodeA.addChild(0, nodeB1);
//
//        //Stream from here node C9 should yield a stream of:
//        //C8, C7
//        try
//        {
//            Stream<GCodeEventNode> result = nodeC9.streamSiblingsBackwardsFromHere();
//
//            ArrayList<GCodeEventNode> resultList = new ArrayList<>();
//            result.forEach(node ->
//            {
//                System.out.println("Adding node " + node.toString());
//                resultList.add(node);
//            });
//
//            assertEquals(2, resultList.size());
//            assertSame(nodeC8, resultList.get(0));
//            assertSame(nodeC7, resultList.get(1));
//        } catch (NodeProcessingException ex)
//        {
//            fail("Node processing exception");
//        }
//    }
//
//    /**
//     * Test of streamSiblingsFromHere method, of class GCodeEventNode.
//     */
//    @Test
//    public void testStreamSiblingsAndMeFromHere()
//    {
//        System.out.println("streamSiblingsAndMeFromHere");
//        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
//        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
//        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
//        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
//        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
//        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
//        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
//        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
//        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
//        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
//        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
//        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
//        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
//
//        nodeB1.addChild(0, nodeC3);
//        nodeB1.addChild(0, nodeC2);
//        nodeB1.addChild(0, nodeC1);
//
//        nodeB2.addChild(0, nodeC6);
//        nodeB2.addChild(0, nodeC5);
//        nodeB2.addChild(0, nodeC4);
//
//        nodeB3.addChild(0, nodeC9);
//        nodeB3.addChild(0, nodeC8);
//        nodeB3.addChild(0, nodeC7);
//
//        nodeA.addChild(0, nodeB3);
//        nodeA.addChild(0, nodeB2);
//        nodeA.addChild(0, nodeB1);
//
//        //Stream from here node C5 should yield a stream of:
//        //C5, C6
//        try
//        {
//            Stream<GCodeEventNode> result = nodeC5.streamSiblingsAndMeFromHere();
//
//            ArrayList<GCodeEventNode> resultList = new ArrayList<>();
//            result.forEach(node ->
//            {
//                System.out.println("Adding node " + node.toString());
//                resultList.add(node);
//            });
//
//            assertEquals(2, resultList.size());
//            assertSame(nodeC5, resultList.get(0));
//            assertSame(nodeC6, resultList.get(1));
//        } catch (NodeProcessingException ex)
//        {
//            fail("Node processing exception");
//        }
//    }
//
//    /**
//     * Test of streamSiblingsBackwardsFromHere method, of class GCodeEventNode.
//     */
//    @Test
//    public void testStreamSiblingsBackwardsAndMeFromHere()
//    {
//        System.out.println("streamSiblingsAndMeBackwardsFromHere");
//        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
//        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
//        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
//        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
//        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
//        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
//        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
//        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
//        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
//        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
//        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
//        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
//        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
//
//        nodeB1.addChild(0, nodeC3);
//        nodeB1.addChild(0, nodeC2);
//        nodeB1.addChild(0, nodeC1);
//
//        nodeB2.addChild(0, nodeC6);
//        nodeB2.addChild(0, nodeC5);
//        nodeB2.addChild(0, nodeC4);
//
//        nodeB3.addChild(0, nodeC7);
//        nodeB3.addChild(1, nodeC8);
//        nodeB3.addChild(2, nodeC9);
//
//        nodeA.addChild(0, nodeB3);
//        nodeA.addChild(0, nodeB2);
//        nodeA.addChild(0, nodeB1);
//
//        //Stream from here node C9 should yield a stream of:
//        //C9, C8, C7
//        try
//        {
//            Stream<GCodeEventNode> result = nodeC9.streamSiblingsAndMeBackwardsFromHere();
//
//            ArrayList<GCodeEventNode> resultList = new ArrayList<>();
//            result.forEach(node ->
//            {
//                System.out.println("Adding node " + node.toString());
//                resultList.add(node);
//            });
//
//            assertEquals(3, resultList.size());
//            assertSame(nodeC9, resultList.get(0));
//            assertSame(nodeC8, resultList.get(1));
//            assertSame(nodeC7, resultList.get(2));
//        } catch (NodeProcessingException ex)
//        {
//            fail("Node processing exception");
//        }
//    }
//

    /**
     * Test of addSiblingBefore method, of class GCodeEventNode.
     */
    @Test
    public void testAddSiblingBefore()
    {
        System.out.println("addSiblingBefore");

        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        GCodeEventNode insertedNode = new GCodeEventNodeTestImpl("InsertedNode");

        nodeC7.addSiblingBefore(insertedNode);

        assertEquals(4, nodeB3.children.size());
        assertSame(insertedNode, nodeB3.children.get(0));
        assertSame(nodeC7, nodeB3.children.get(1));
        assertSame(nodeC8, nodeB3.children.get(2));
        assertSame(nodeC9, nodeB3.children.get(3));
    }

    /**
     * Test of addSiblingAfter method, of class GCodeEventNode.
     */
    @Test
    public void testAddSiblingAfter()
    {
        System.out.println("addSiblingAfter");

        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        GCodeEventNode insertedNode = new GCodeEventNodeTestImpl("InsertedNode");

        nodeC7.addSiblingAfter(insertedNode);

        assertEquals(4, nodeB3.children.size());
        assertSame(nodeC7, nodeB3.children.get(0));
        assertSame(insertedNode, nodeB3.children.get(1));
        assertSame(nodeC8, nodeB3.children.get(2));
        assertSame(nodeC9, nodeB3.children.get(3));
    }

    /**
     * Test of addSiblingAfter method, of class GCodeEventNode. Add the sibling
     * as the last element
     */
    @Test
    public void testAddSiblingAfter_lastElement()
    {
        System.out.println("addSiblingAfter");

        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        GCodeEventNode insertedNode = new GCodeEventNodeTestImpl("InsertedNode");

        assertEquals(3, nodeC7.children.size());

        nodeD3.addSiblingAfter(insertedNode);

        assertEquals(4, nodeC7.children.size());
        assertSame(nodeD1, nodeC7.children.get(0));
        assertSame(nodeD2, nodeC7.children.get(1));
        assertSame(nodeD3, nodeC7.children.get(2));
        assertSame(insertedNode, nodeC7.children.get(3));
    }
//
//    /**
//     * Test of removeFromParent method, of class GCodeEventNode.
//     */
//    @Test
//    public void testRemoveFromParent()
//    {
//        System.out.println("removeFromParent");
//
//        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
//        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
//        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
//        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
//        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
//        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
//        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
//        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
//        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
//        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
//        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
//        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
//        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
//        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
//        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
//        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");
//
//        nodeB1.addChild(0, nodeC3);
//        nodeB1.addChild(0, nodeC2);
//        nodeB1.addChild(0, nodeC1);
//
//        nodeB2.addChild(0, nodeC6);
//        nodeB2.addChild(0, nodeC5);
//        nodeB2.addChild(0, nodeC4);
//
//        nodeB3.addChild(0, nodeC9);
//        nodeB3.addChild(0, nodeC8);
//        nodeB3.addChild(0, nodeC7);
//
//        nodeC7.addChild(0, nodeD3);
//        nodeC7.addChild(0, nodeD2);
//        nodeC7.addChild(0, nodeD1);
//
//        nodeA.addChild(0, nodeB3);
//        nodeA.addChild(0, nodeB2);
//        nodeA.addChild(0, nodeB1);
//
//        assertEquals(3, nodeC7.getChildren().size());
//
//        nodeD3.removeFromParentAndFixup();
//
//        List<GCodeEventNode> children = nodeC7.getChildren();
//
//        assertEquals(2, children.size());
//        assertSame(nodeD1, children.get(0));
//        assertSame(nodeD2, children.get(1));
//    }
//

    /**
     * Test of addChildAtEnd method, of class GCodeEventNode.
     */
    @Test
    public void testAddChildAtEnd()
    {
        System.out.println("addChildAtEnd");

        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        assertEquals(3, nodeC7.children.size());

        GCodeEventNode addedNode = new GCodeEventNodeTestImpl("AddedNode");

        nodeC7.addChildAtEnd(addedNode);

        assertEquals(4, nodeC7.children.size());
        assertSame(nodeD1, nodeC7.children.get(0));
        assertSame(nodeC7, nodeC7.children.get(0).getParent().get());
        assertSame(nodeD2, nodeC7.children.get(1));
        assertSame(nodeC7, nodeC7.children.get(1).getParent().get());
        assertSame(nodeD3, nodeC7.children.get(2));
        assertSame(nodeC7, nodeC7.children.get(2).getParent().get());
        assertSame(addedNode, nodeC7.children.get(3));
        assertSame(nodeC7, nodeC7.children.get(3).getParent().get());

        assertSame(nodeB3, nodeC7.getParent().get());
    }

    /**
     * Test of getSiblingBefore method, of class GCodeEventNode.
     */
    @Test
    public void testGetSiblingBefore()
    {
        System.out.println("getSiblingBefore");

        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        Optional<GCodeEventNode> result1 = nodeC8.getSiblingBefore();
        assertTrue(result1.isPresent());
        assertSame(nodeC7, result1.get());

        Optional<GCodeEventNode> result2 = nodeC4.getSiblingBefore();
        assertFalse(result2.isPresent());
    }

    /**
     * Test of getSiblingAfter method, of class GCodeEventNode.
     */
    @Test
    public void testGetSiblingAfter()
    {
        System.out.println("getSiblingAfter");

        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        Optional<GCodeEventNode> result1 = nodeC7.getSiblingAfter();
        assertTrue(result1.isPresent());
        assertSame(nodeC8, result1.get());

        Optional<GCodeEventNode> result2 = nodeB3.getSiblingAfter();
        assertFalse(result2.isPresent());
    }

    /**
     * Test of getAbsolutelyTheLastEvent method, of class GCodeEventNode.
     */
    @Test
    public void testGetAbsolutelyTheLastEvent()
    {
        System.out.println("getAbsolutelyTheLastEvent");

        GCodeEventNode nodeA = new GCodeEventNodeTestImpl("nodeA");
        GCodeEventNode nodeB1 = new GCodeEventNodeTestImpl("nodeB1");
        GCodeEventNode nodeB2 = new GCodeEventNodeTestImpl("nodeB2");
        GCodeEventNode nodeB3 = new GCodeEventNodeTestImpl("nodeB3");
        GCodeEventNode nodeC1 = new GCodeEventNodeTestImpl("nodeC1");
        GCodeEventNode nodeC2 = new GCodeEventNodeTestImpl("nodeC2");
        GCodeEventNode nodeC3 = new GCodeEventNodeTestImpl("nodeC3");
        GCodeEventNode nodeC4 = new GCodeEventNodeTestImpl("nodeC4");
        GCodeEventNode nodeC5 = new GCodeEventNodeTestImpl("nodeC5");
        GCodeEventNode nodeC6 = new GCodeEventNodeTestImpl("nodeC6");
        GCodeEventNode nodeC7 = new GCodeEventNodeTestImpl("nodeC7");
        GCodeEventNode nodeC8 = new GCodeEventNodeTestImpl("nodeC8");
        GCodeEventNode nodeC9 = new GCodeEventNodeTestImpl("nodeC9");
        GCodeEventNode nodeD1 = new GCodeEventNodeTestImpl("nodeD1");
        GCodeEventNode nodeD2 = new GCodeEventNodeTestImpl("nodeD2");
        GCodeEventNode nodeD3 = new GCodeEventNodeTestImpl("nodeD3");

        nodeB1.addChildAtEnd(nodeC1);
        nodeB1.addChildAtEnd(nodeC2);
        nodeB1.addChildAtEnd(nodeC3);

        nodeB2.addChildAtEnd(nodeC4);
        nodeB2.addChildAtEnd(nodeC5);
        nodeB2.addChildAtEnd(nodeC6);

        nodeB3.addChildAtEnd(nodeC7);
        nodeB3.addChildAtEnd(nodeC8);
        nodeB3.addChildAtEnd(nodeC9);

        nodeC7.addChildAtEnd(nodeD1);
        nodeC7.addChildAtEnd(nodeD2);
        nodeC7.addChildAtEnd(nodeD3);

        nodeA.addChildAtEnd(nodeB1);
        nodeA.addChildAtEnd(nodeB2);
        nodeA.addChildAtEnd(nodeB3);

        GCodeEventNode result1 = nodeA.getAbsolutelyTheLastEvent();
        assertNotNull(result1);
        assertSame(nodeC9, result1);
    }
}
