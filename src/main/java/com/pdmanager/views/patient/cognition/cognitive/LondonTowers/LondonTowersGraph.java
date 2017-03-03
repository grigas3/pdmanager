package com.pdmanager.views.patient.cognition.cognitive.LondonTowers;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.List;


public class LondonTowersGraph
{
	private SimpleGraph<String, DefaultEdge> g;

	public static final int
		R = 1,
		G = 2,
		B = 3;
	
	public static final String
		aa="aa", ab="ab", ac="ac", ad="ad", ae="ae", af="af", ag="ag", ah="ah", ai="ai", aj="aj",
		ba="ba", bb="bb", bc="bc", bd="bd", be="be", bf="bf", bg="bg", bh="bh", bi="bi", bj="bj",
		ca="ca", cb="cb", cc="cc", cd="cd", ce="ce", cf="cf", cg="cg", ch="ch", ci="ci", cj="cj",
		da="da", db="db", dc="dc", dd="dd", de="de", df="df", dg="dg", dh="dh", di="di", dj="dj",
		ea="ea", eb="eb", ec="ec", ed="ed", ee="ee", ef="ef", eg="eg", eh="eh", ei="ei", ej="ej",
		fa="fa", fb="fb", fc="fc", fd="fd", fe="fe", ff="ff", fg="fg", fh="fh", fi="fi", fj="fj";

	public static final TowerStacks
		taa = new TowerStacks(new PS(), new PS(), new PS(R, G, B)),
		tab = new TowerStacks(new PS(), new PS(R, G, B), new PS()),
		tac = new TowerStacks(new PS(R, G, B), new PS(), new PS()),
		tad = new TowerStacks(new PS(B), new PS(), new PS(R, G)),
		tae = new TowerStacks(new PS(), new PS(B), new PS(R, G)),
		taf = new TowerStacks(new PS(), new PS(B, G), new PS(R)),
		tag = new TowerStacks(new PS(G), new PS(B), new PS(R)),
		tah = new TowerStacks(new PS(G), new PS(B, R), new PS()),
		tai = new TowerStacks(new PS(G, R), new PS(B), new PS()),
		taj = new TowerStacks(new PS(G, R), new PS(), new PS(B)),
		
		tba = new TowerStacks(new PS(), new PS(), new PS(R, B, G)),
		tbb = new TowerStacks(new PS(), new PS(R, B, G), new PS()),
		tbc = new TowerStacks(new PS(R, B, G), new PS(), new PS()),
		tbd = new TowerStacks(new PS(G), new PS(), new PS(R, B)),
		tbe = new TowerStacks(new PS(), new PS(G), new PS(R, B)),
		tbf = new TowerStacks(new PS(), new PS(G, B), new PS(R)),
		tbg = new TowerStacks(new PS(B), new PS(G), new PS(R)),
		tbh = new TowerStacks(new PS(B), new PS(G, R), new PS()),
		tbi = new TowerStacks(new PS(B, R), new PS(G), new PS()),
		tbj = new TowerStacks(new PS(B, R), new PS(), new PS(G)),
		
		tca = new TowerStacks(new PS(), new PS(), new PS(B, R, G)),
		tcb = new TowerStacks(new PS(), new PS(B, R, G), new PS()),
		tcc = new TowerStacks(new PS(B, R, G), new PS(), new PS()),
		tcd = new TowerStacks(new PS(G), new PS(), new PS(B, R)),
		tce = new TowerStacks(new PS(), new PS(G), new PS(B, R)),
		tcf = new TowerStacks(new PS(), new PS(G, R), new PS(B)),
		tcg = new TowerStacks(new PS(R), new PS(G), new PS(B)),
		tch = new TowerStacks(new PS(R), new PS(G, B), new PS()),
		tci = new TowerStacks(new PS(R, B), new PS(G), new PS()),
		tcj = new TowerStacks(new PS(R, B), new PS(), new PS(G)),
		
		tda = new TowerStacks(new PS(), new PS(), new PS(B, G, R)),
		tdb = new TowerStacks(new PS(), new PS(B, G, R), new PS()),
		tdc = new TowerStacks(new PS(B, G, R), new PS(), new PS()),
		tdd = new TowerStacks(new PS(R), new PS(), new PS(B, G)),
		tde = new TowerStacks(new PS(), new PS(R), new PS(B, G)),
		tdf = new TowerStacks(new PS(), new PS(R, G), new PS(B)),
		tdg = new TowerStacks(new PS(G), new PS(R), new PS(B)),
		tdh = new TowerStacks(new PS(G), new PS(R, B), new PS()),
		tdi = new TowerStacks(new PS(G, B), new PS(R), new PS()),
		tdj = new TowerStacks(new PS(G, B), new PS(), new PS(R)),
		
		tea = new TowerStacks(new PS(), new PS(), new PS(G, B, R)),
		teb = new TowerStacks(new PS(), new PS(G, B, R), new PS()),
		tec = new TowerStacks(new PS(G, B, R), new PS(), new PS()),
		ted = new TowerStacks(new PS(R), new PS(), new PS(G, B)),
		tee = new TowerStacks(new PS(), new PS(R), new PS(G, B)),
		tef = new TowerStacks(new PS(), new PS(R, B), new PS(G)),
		teg = new TowerStacks(new PS(B), new PS(R), new PS(G)),
		teh = new TowerStacks(new PS(B), new PS(R, G), new PS()),
		tei = new TowerStacks(new PS(B, G), new PS(R), new PS()),
		tej = new TowerStacks(new PS(B, G), new PS(), new PS(R)),

		tfa = new TowerStacks(new PS(), new PS(), new PS(G, R, B)),
		tfb = new TowerStacks(new PS(), new PS(G, R, B), new PS()),
		tfc = new TowerStacks(new PS(G, R, B), new PS(), new PS()),
		tfd = new TowerStacks(new PS(B), new PS(), new PS(G, R)),
		tfe = new TowerStacks(new PS(), new PS(B), new PS(G, R)),
		tff = new TowerStacks(new PS(), new PS(B, R), new PS(G)),
		tfg = new TowerStacks(new PS(R), new PS(B), new PS(G)),
		tfh = new TowerStacks(new PS(R), new PS(B, G), new PS()),
		tfi = new TowerStacks(new PS(R, G), new PS(B), new PS()),
		tfj = new TowerStacks(new PS(R, G), new PS(), new PS(B));
		
	public static final String[] graphPositions =
	{
		aa, ab, ac, ad, ae, af, ag, ah, ai, aj,
		ba, bb, bc, bd, be, bf, bg, bh, bi, bj,
		ca, cb, cc, cd, ce, cf, cg, ch, ci, cj,
		da, db, dc, dd, de, df, dg, dh, di, dj,
		ea, eb, ec, ed, ee, ef, eg, eh, ei, ej,
		fa, fb, fc, fd, fe, ff, fg, fh, fi, fj
	};
	
	public static final TowerStacks[] gamePositions =
	{
		taa, tab, tac, tad, tae, taf, tag, tah, tai, taj,
		tba, tbb, tbc, tbd, tbe, tbf, tbg, tbh, tbi, tbj,
		tca, tcb, tcc, tcd, tce, tcf, tcg, tch, tci, tcj,
		tda, tdb, tdc, tdd, tde, tdf, tdg, tdh, tdi, tdj,
		tea, teb, tec, ted, tee, tef, teg, teh, tei, tej,
		tfa, tfb, tfc, tfd, tfe, tff, tfg, tfh, tfi, tfj
	};
	
	public int getPathLenDijkstra(String n0, String n1)
	{
		List<DefaultEdge> list = DijkstraShortestPath.findPathBetween(g, n0, n1);
//		for (DefaultEdge de:list)
//			System.out.println(g.getEdgeSource(de) + " - " + g.getEdgeTarget(de));
		return list.size();
	}
	
	private void initializeGraph()
	{
		//Adding the nodes from "aa" to "fj"
		for (char i='a'; i<='f'; i++)
			for (char j='a'; j<='j'; j++)
				g.addVertex(""+i+""+j);
		
	    // Adding the edges to create the circuit
	    g.addEdge(aj, fc);
	    g.addEdge(bj, cc);
	    g.addEdge(cj, bc);
	    g.addEdge(dj, ec);
	    g.addEdge(ej, dc);
	    g.addEdge(fj, ac);
	    
	    g.addEdge(ae, aa); g.addEdge(ae, af);
	    g.addEdge(be, ba); g.addEdge(be, bf);
	    g.addEdge(ce, ca); g.addEdge(ce, cf);
	    g.addEdge(de, da); g.addEdge(de, df);
	    g.addEdge(ee, ea); g.addEdge(ee, ef);
	    g.addEdge(fe, fa); g.addEdge(fe, ff);
	    
	    g.addEdge(af, db); g.addEdge(af, fh);
	    g.addEdge(bf, eb); g.addEdge(bf, ch);
	    g.addEdge(cf, fb); g.addEdge(cf, bh);
	    g.addEdge(df, ab); g.addEdge(df, eh);
	    g.addEdge(ef, bb); g.addEdge(ef, dh);
	    g.addEdge(ff, cb); g.addEdge(ff, ah);
	    
	    g.addEdge(ah, ai); g.addEdge(ah, cb);
	    g.addEdge(bh, bi); g.addEdge(bh, fb);
	    g.addEdge(ch, ci); g.addEdge(ch, eb);
	    g.addEdge(dh, di); g.addEdge(dh, bb);
	    g.addEdge(eh, ei); g.addEdge(eh, ab);
	    g.addEdge(fh, fi); g.addEdge(fh, db);
	    
	    g.addEdge(ai, aj); g.addEdge(ai, fc);
	    g.addEdge(bi, bj); g.addEdge(bi, cc);
	    g.addEdge(ci, cj); g.addEdge(ci, bc);
	    g.addEdge(di, dj); g.addEdge(di, ec);
	    g.addEdge(ei, ej); g.addEdge(ei, dc);
	    g.addEdge(fi, fj); g.addEdge(fi, ac);
	    
	    g.addEdge(ad, aa); g.addEdge(ad, ae); g.addEdge(ad, ej);
	    g.addEdge(bd, ba); g.addEdge(bd, be); g.addEdge(bd, dj);
	    g.addEdge(cd, ca); g.addEdge(cd, ce); g.addEdge(cd, aj);
	    g.addEdge(dd, da); g.addEdge(dd, de); g.addEdge(dd, fj);
	    g.addEdge(ed, ea); g.addEdge(ed, ee); g.addEdge(ed, cj);
	    g.addEdge(fd, fa); g.addEdge(fd, fe); g.addEdge(fd, bj);
	    
	    g.addEdge(ag, bd); g.addEdge(ag, ae); g.addEdge(ag, af);
	    g.addEdge(ag, ah); g.addEdge(ag, ai); g.addEdge(ag, dj);
	    
	    g.addEdge(bg, ad); g.addEdge(bg, be); g.addEdge(bg, bf);
	    g.addEdge(bg, bh); g.addEdge(bg, bi); g.addEdge(bg, ej);
	    
	    g.addEdge(cg, dd); g.addEdge(cg, ce); g.addEdge(cg, cf);
	    g.addEdge(cg, ch); g.addEdge(cg, ci); g.addEdge(cg, fj);
	    
	    g.addEdge(dg, cd); g.addEdge(dg, de); g.addEdge(dg, df);
	    g.addEdge(dg, dh); g.addEdge(dg, di); g.addEdge(dg, aj);
	    
	    g.addEdge(eg, fd); g.addEdge(eg, ee); g.addEdge(eg, ef);
	    g.addEdge(eg, eh); g.addEdge(eg, ei); g.addEdge(eg, bj);
	    
	    g.addEdge(fg, ed); g.addEdge(fg, fe); g.addEdge(fg, ff);
	    g.addEdge(fg, fh); g.addEdge(fg, fi); g.addEdge(fg, cj);
	    
	}
	
	public LondonTowersGraph()
	{
		g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		initializeGraph();	    
	}
}
