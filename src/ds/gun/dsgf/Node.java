/**
 * 
 */
package ds.gun.dsgf;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import robocode.RobocodeFileOutputStream;
import ds.Hud;
import ds.Versatile;
import ds.constant.ConstantManager;
import ds.gun.VirtualBullet;

/**
 * @author f4
 * 
 */
public class Node implements Comparable<Node> {

	/**
	 * @author f4
	 * 
	 */
	protected class PicDescription {

		public int m_nPicIndex;
		public int m_nPicSize;
		public int m_nPicSizeRaw;

		/**
		 * @param nPicSizeRaw
		 * @param bestPicIndex
		 * @param bestPicSize
		 */
		public PicDescription(int nPicIndex, int nPicSize, int nPicSizeRaw) {
			m_nPicIndex = nPicIndex;
			m_nPicSize = nPicSize;
			m_nPicSizeRaw = nPicSizeRaw;
		}
	}

	/**
	 * liste des feuilles
	 */
	private ArrayList<Leaf> m_childLeafs;

	/**
	 * liste des nodes filles
	 */
	private ArrayList<Node> m_childNodes;

	/**
	 * tableau des compteurs de hit pour chaque angle tableau construit a partir
	 * des feuilles ou maintenu a jour par le noeud avec rolling average si l'on
	 * est sur un noeud sans feuille (final)
	 */
	private long[] m_samplesSummary;

	/**
	 * 
	 */
	private int m_nbSamples;

	/**
	 * nombre de feuilles max
	 */
	private int m_maxNodeLeafCount;
	
	/**
	 * nombre de feuilles max Effectif (peut etre augment�)
	 */
	private int m_effectiveMaxNodeLeafCount;

	/**
	 * index de l'info utilisee pour la segmentation
	 */
	private int m_segmentingInfoIndex;

	/**
	 * limite sur la valeur de segmentation choisie pour ce noeud
	 */
	private double m_threshold;

	/**
	 * parent node
	 */
	private Node m_parent;

	/**
	 * nombre de pas de divisions lors de la recherche de la meilleure division
	 */
	private int m_segmentationFactor;

	/**
	 * taille minimale
	 */
	private int m_minNodeLeafCount;

	/**
	 * parametre pour l'estimation de la densité
	 */
	private int m_densityEstimationWindow;

	/**
	 * doit enregistrer les noeuds, ou pas
	 */
	private boolean m_bIsDataSaver;

	public Node(Node parent, double threshold, int maxNodeLeafCount,
			int minNodeLeafCount, boolean bIsDataSaver) {
		Build(parent, threshold, maxNodeLeafCount, minNodeLeafCount,
				bIsDataSaver);
	}

	public Node(Node parent, int maxNodeLeafCount, int minNodeLeafCount,
			boolean bIsDataSaver) {
		Build(parent, Double.NEGATIVE_INFINITY, maxNodeLeafCount,
				minNodeLeafCount, bIsDataSaver);
	}

	protected Node(Node parent, double threshold, boolean bIsDataSaver) {
		Build(parent, threshold, parent.m_maxNodeLeafCount,
				parent.m_minNodeLeafCount, bIsDataSaver);
	}

	protected Node(Node parent, boolean bIsDataSaver) {
		Build(parent, Double.NEGATIVE_INFINITY, parent.m_maxNodeLeafCount,
				parent.m_minNodeLeafCount, bIsDataSaver);
	}

	private void Build(Node parent, double threshold, int maxNodeLeafCount,
			int minNodeLeafCount, boolean bIsDataSaver) {
		m_parent = parent;

		m_bIsDataSaver = bIsDataSaver;

		ConstantManager cm = ConstantManager.getInstance();

		m_nbSamples = (int) cm.getIntegerConstant("gun.dsgf.nbSamples")
				.longValue();
		m_segmentationFactor = (int) cm.getIntegerConstant(
				"gun.dsgf.segmentationFactor").longValue();
		m_densityEstimationWindow = (int) (1 + cm.getIntegerConstant(
				"gun.dsgf.densityEstimationWindow").longValue());

		m_maxNodeLeafCount = maxNodeLeafCount;
		m_effectiveMaxNodeLeafCount = m_maxNodeLeafCount;

		m_minNodeLeafCount = minNodeLeafCount;

		m_childLeafs = new ArrayList<Leaf>();
		m_childNodes = new ArrayList<Node>();
		m_samplesSummary = new long[m_nbSamples];

		m_segmentingInfoIndex = 0;

		m_threshold = threshold;

		// si on est le noeud racine
		if (parent == null) {
			m_childNodes.add(new Node(this, threshold, m_bIsDataSaver));
		}
	}

	/**
	 * Ajout d'une statistique
	 * 
	 * @param vb
	 *            balle virtuelle associee
	 */
	public void add(IndexedVirtualBullet vb) {
		// on a un hit
		Leaf leaf = new Leaf(this, vb);
		add(leaf);
	}

	private void splitNode(Node node, Node sub1, Node sub2) {
		// System.out.println( "spliting nodes" );

		m_childNodes.remove(node);
		m_childNodes.add(sub1);
		m_childNodes.add(sub2);

		saveNodeToFile(getSegmentingInfoIndex(), node, sub1, sub2);

		Collections.sort(m_childNodes);
	}

	/**
	 * @param node
	 * @param string
	 */
	private void saveNodeToFile(int segmentingInfoIndex, Node node, Node sub1, Node sub2)
	{
		if (m_bIsDataSaver
				&& ConstantManager.getInstance().getBooleanConstant("debugData")) {
			try {
				// ZipOutputStream zipout =
				// ((Versatile)Versatile.getMe()).getZipout();
				// zipout.putNextEntry(new ZipEntry(entryName + ".csv"));
				// OutputStream out = new BufferedOutputStream( zipout );
				RobocodeFileOutputStream zipout = new RobocodeFileOutputStream(
						Versatile.getMe().getDataFile("data.csv").toString(),
						true);
				Writer out = new BufferedWriter(new OutputStreamWriter(zipout));
				out.write("\n\n\n");
				out.write(",dimension," + segmentingInfoIndex
						+ "," + SegmentationInfo.getDimensionName( segmentingInfoIndex )
						+ "\n");
				out.write("," + System.identityHashCode(this) + ",original,");
				for (long sample : node.getSamples())
					out.write("" + sample + ",");
				out.write(",," + node.getCrestFactor());
				out.write("\n");
				out.write("," + System.identityHashCode(sub1) + ",sub1,");
				for (long sample : sub1.getSamples())
					out.write("" + sample + ",");
				out.write(",," + sub1.getCrestFactor());
				out.write("\n");
				out.write("," + System.identityHashCode(sub2) + ",sub2,");
				for (long sample : sub2.getSamples())
					out.write("" + sample + ",");
				out.write(",," + sub2.getCrestFactor());
				out.write("\n");
				out.write("\n");
				out.flush();
				// zipout.closeEntry();
				out.close();
				zipout.close();
			} catch (IOException e) {
				System.out.println("Error saving factors:" + e);
			}
		}
	}

	/**
	 * Ajoute une feuille au noeud; Si le noeud n'est pas un noeud final :
	 * ajoute la feuille au noeud fils concerne; Si le noeud est un noeud final
	 * : ajoute la feuille puis v�rifie s'il faut couper
	 * 
	 * @param leaf
	 */
	private void add(Leaf leaf) {
		if (!m_childNodes.isEmpty()) {
			double infoValue = leaf.getSegmentationInfo().getInfo(m_segmentingInfoIndex);
			// parcours des nodes filles
			for (Node node : m_childNodes) {
				if (infoValue >= node.getThreshold()) {
					node.add(leaf);
					return;
				}
			}
		} else {
			m_childLeafs.add(leaf);
			// mise a jour des samples
			updateSamples();

			// verification s'il faut couper le noeud
			if (m_childLeafs.size() > m_effectiveMaxNodeLeafCount) {
				int maxChildNodeCount = (int) ConstantManager.getInstance()
						.getIntegerConstant("gun.dsgf.maxChildNodeCount")
						.longValue();
				if (m_parent.m_childNodes.size() >= maxChildNodeCount) {
					// on doit descendre
					Node node = new Node(this, m_bIsDataSaver);
					node.m_childLeafs = this.m_childLeafs;
					//this.m_childLeafs.clear(); // bug?!
					this.m_childLeafs = new ArrayList<Leaf>();
					m_childNodes.add(node);
					// System.out.println( "Going deep" );
				} else {
					// on doit couper
					if (m_parent.m_childNodes.size() == 1) {
						searchSplitDimension();
						if (ConstantManager.getInstance().getBooleanConstant(
								"debug"))
							System.out.println("Choosing dimension "
									+ m_parent.m_segmentingInfoIndex);
					}
					Node sub1 = new Node(m_parent, m_threshold, m_bIsDataSaver);
					Node sub2 = new Node(m_parent, 0, m_bIsDataSaver);
					SplitSolution split = searchSplit(sub1, sub2);
					if (sub1.m_childLeafs.size() == 0
							|| sub2.m_childLeafs.size() == 0) {
						System.out.println("Alerte BUG!!!");
						split = searchSplit(sub1, sub2);
					}
					m_parent.splitNode(this, sub1, sub2);
				}
			}
		}
	}

	/**
	 * met a jour le tableau des samples
	 */
	private void updateSamples() {
		// TODO: ne pas boucler pour refaire les samples a chaque ajout
		m_samplesSummary = new long[m_nbSamples];
		for (Leaf l : m_childLeafs) {
			int sampleIndex = l.getAngleIndex();
			m_samplesSummary[(int) sampleIndex]++;
		}
	}

	/**
	 * cherche la meilleure dimension pour couper le noeud
	 */
	private void searchSplitDimension() {
		int nbDimensions = m_childLeafs.get(0).getSegmentationInfo()
				.getDImCount();
		int bestDim = 0;
		double bestVar = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < nbDimensions; i++) {
			m_parent.m_segmentingInfoIndex = i;
			Node sub1 = new Node(m_parent, m_threshold, m_bIsDataSaver);
			Node sub2 = new Node(m_parent, 0, m_bIsDataSaver);
			SplitSolution split = searchSplit(sub1, sub2);
			double sumVar = split.m_variance;
			/*
			 * long bestSampleIndex1 = sub1.getSamplesPic().m_nPicIndex; long
			 * bestSampleIndex2 = sub2.getSamplesPic().m_nPicIndex; long delta =
			 * Math.abs( bestSampleIndex1 - bestSampleIndex2); if ( delta < 3 )
			 * sumVar *= 1+(3-delta*0.3);
			 */
			if (sumVar > bestVar /*
								 * && sub1.m_childLeafs.size() > 20 &&
								 * sub2.m_childLeafs.size() > 20
								 */) {
				bestDim = i;
				bestVar = sumVar;
			}
		}
		m_parent.m_segmentingInfoIndex = bestDim;
	}

	public class SplitSolution {
		public double m_variance = 0;
		public double m_threshold = 0;

		public SplitSolution(double variance, double threshold) {
			m_variance = variance;
			m_threshold = threshold;
		}
	}

	/**
	 * cherche la meilleure facon de couper pour avoir une variance faible
	 * 
	 * @param sub1
	 *            node 1 de la coupure
	 * @param sub2
	 *            node 2 de la coupute
	 * @return la variance du couple (le max)
	 */
	private SplitSolution searchSplit(Node sub1, Node sub2) {
		// recherche du meilleur moyen pour couper
		// TODO: trouver mieux que couper au milieu!
		int segmentingInfoIndex;
		if (m_parent != null)
			segmentingInfoIndex = m_parent.m_segmentingInfoIndex;
		else
			segmentingInfoIndex = 0;
		// tri de la liste
		Collections.sort(m_childLeafs, new LeafComparator(segmentingInfoIndex));

		int end = m_childLeafs.size();
		ArrayList<Leaf> bestCut1 = new ArrayList<Leaf>();
		ArrayList<Leaf> bestCut2 = new ArrayList<Leaf>();
		double bestVar = Double.NEGATIVE_INFINITY;
		double bestThreshold = 0;

		for (int i = 1; i < m_segmentationFactor; i++) {/*
														 * int thresholdIndex =
														 * iend
														 * /segmentationFactor;
														 * sub1
														 * .setLeafs(m_childLeafs
														 * .subList(0,
														 * thresholdIndex));
														 * sub2
														 * .setLeafs(m_childLeafs
														 * .
														 * subList(thresholdIndex
														 * , end));
														 * sub2.m_threshold =
														 * m_childLeafs
														 * .get(thresholdIndex
														 * ).getSegmentationInfo
														 * ().getInfo(
														 * segmentingInfoIndex);
														 * sub1.updateSamples();
														 * sub2.updateSamples();
														 */
			double min = m_childLeafs.get(0).getSegmentationInfo()
					.getInfo(segmentingInfoIndex);
			double max = m_childLeafs.get(end - 1).getSegmentationInfo()
					.getInfo(segmentingInfoIndex);
			double threshold = min + i * (max - min) / m_segmentationFactor;
			int thresholdIndex = 0;
			for (int j = 0; j < end; j++) {
				if (m_childLeafs.get(j).getSegmentationInfo()
						.getInfo(segmentingInfoIndex) >= threshold) {
					thresholdIndex = j;
					break;
				}
			}
			sub1.setLeafs(m_childLeafs.subList(0, thresholdIndex));
			sub2.setLeafs(m_childLeafs.subList(thresholdIndex, end));
			sub2.m_threshold = threshold;
			sub1.updateSamples();
			sub2.updateSamples();

			/*
			 * max marche mieux que min
			 */
			// double sumVar = Math.max( sub1.getSamplesVariance(), sub2
			// .getSamplesVariance() );
			
			//double sumVar = sub1.getSamplesVariance()
			// * sub2.getSamplesVariance();
			
			// double sumVar = getRessemblance( sub1, sub2 );
			/*
			 * int pic1 = sub1.getSamplesPic().m_nPicSizeRaw; int pic2 =
			 * sub2.getSamplesPic().m_nPicSizeRaw; double sumVar =
			 * sub1.getSamplesVariance()/pic1 + sub2.getSamplesVariance()/pic2;
			 */

			double crest1 = sub1.getCrestFactor();
			double crest2 = sub2.getCrestFactor();
			double sumVar = crest1 + crest2;
			sumVar *= getDifference(sub1, sub2);

			/*
			 * si les intervalles sont invalides met une valeur de SumVar très
			 * haute ce découpage ne doit pas etre choisi à moins d'etre le
			 * seul ^^
			 */
			// pas de differences sur la valeur a segmenter
			if (min == max) {
				// sumVar = 10000;
				sumVar /= 10;
			}
			// segments trops petits
			else if (sub1.m_childLeafs.size() < m_minNodeLeafCount
					|| sub2.m_childLeafs.size() < m_minNodeLeafCount) {
				// sumVar = 9000;
				sumVar /= 2;
			}
			// si on a un meilleur score que les précédents, nouveau meilleur
			// score pour ce découpage
			if (sumVar > bestVar) {
				bestCut1 = (ArrayList<Leaf>) sub1.m_childLeafs.clone();
				bestCut2 = (ArrayList<Leaf>) sub2.m_childLeafs.clone();
				bestVar = sumVar;
				bestThreshold = threshold;
			}
		}
		sub1.m_childLeafs = bestCut1;
		sub2.m_childLeafs = bestCut2;
		sub1.updateSamples();
		sub2.updateSamples();
		sub2.m_threshold = bestThreshold;

		SplitSolution split = new SplitSolution(bestVar, bestThreshold);
		return split;
	}

	/*
	 * @return
	 */
	/*
	 * private double getSamplesPicHeight() { int i; long nPicHeight = 0;
	 * 
	 * for( i = 1; i < m_nbSamples-1; i++ ) { if( m_samplesSummary[i] >
	 * nPicHeight ) { nPicHeight = m_samplesSummary[i]; } }
	 * 
	 * return nPicHeight; }
	 */

	/**
	 * @return
	 */
	private double getCrestFactor() {
		double sommeDesCarres = 0;

		for (long sample : getSamples()) {
			sommeDesCarres += sample * sample;
		}

		double pic = getSamplesPic().m_nPicSizeRaw;
		double moyenneDesCarres = sommeDesCarres / getSamples().length;
		double rms = Math.sqrt(moyenneDesCarres);

		return pic / rms;
	}

	/**
	 * retourne la difference entre les 2 nodes retourne 1 pour un écart de
	 * pics >= à 6/taille de la vague 0 s'il n'y a aucun écart
	 * 
	 * @param sub1
	 * @param sub2
	 * @return
	 */
	private double getDifference(Node sub1, Node sub2) {
		int i;
		double result = 0;

		result = sub1.getSamplesPic().m_nPicIndex
				- sub2.getSamplesPic().m_nPicIndex;
		result = Math.abs(result);
		result = result * 6 / (double) m_nbSamples;
		if (result > 1)
			result = 1;

		return result;
	}

	/**
	 * remplace la liste des feuilles
	 * 
	 * @param leafList
	 *            liste des feuilles
	 */
	private void setLeafs(List<Leaf> leafList) {
		m_childLeafs.clear();
		m_childLeafs.addAll(leafList);
	}

	/**
	 * Donne la solution de tir
	 */
	public Node getSolutionSamples(SegmentationInfo si) {
		Node noeud;

		// si le noeud contiens des feuilles
		if (!m_childLeafs.isEmpty()) {
			return this;
		} else if (!m_childNodes.isEmpty()) {
			double infoValue = si.getInfo(m_segmentingInfoIndex);
			// parcours des nodes filles
			for (Node node : m_childNodes) {
				if (infoValue >= node.getThreshold()) {
					noeud = node.getSolutionSamples(si);
					return noeud;
				}
			}
		}

		// aucunne solution trouvee ...
		return null;
	}

	/**
	 * Donne la solution de tir
	 */
	public FireIndex getSolution(SegmentationInfo si) {
		FireIndex fi;

		Node noeud = getSolutionSamples(si);
		if (noeud != null) {
			long bestSampleIndex = noeud.getSamplesPic().m_nPicIndex;
			// fiabilite = 1 pour 10 feuilles
			fi = new FireIndex(bestSampleIndex, Math.max(10,
					m_childLeafs.size()) / 10);
			return fi;
		} else {
			// aucunne solution trouvee ...
			fi = new FireIndex((int) m_nbSamples / 2, 0);
			return fi;
		}

	}

	/**
	 * Donne le plus haut (meilleure chance de toucher) sur un sample (tableau
	 * de données)
	 * 
	 * @return
	 */
	private PicDescription getSamplesPic() {
		int bestPicIndex = m_nbSamples / 2; // tire au milieu par defaut
		int bestSampleValue = -1;
		int bestPicSize = 0;
		int bestPicSizeRaw = 0;
		// un noeud avec des feuilles == il n'a pas encore été coupé
		for (int i = 0; i < m_nbSamples; i++) {
			if (m_samplesSummary[i] > bestPicSizeRaw)
				bestPicSizeRaw = (int) m_samplesSummary[i];
			int sampleEstimation = (int) m_samplesSummary[i]
					* m_densityEstimationWindow;
			for (int j = 1; j < m_densityEstimationWindow; j++) {
				if (i - j >= 0)
					sampleEstimation += m_samplesSummary[i - j]
							* (m_densityEstimationWindow - j);
				if (i + j < m_nbSamples)
					sampleEstimation += m_samplesSummary[i + j]
							* (m_densityEstimationWindow - j);
			}
			if (sampleEstimation > bestSampleValue) {
				bestPicIndex = i;
				bestSampleValue = sampleEstimation;
			}
		}
		return new PicDescription(bestPicIndex, bestPicSize, bestPicSizeRaw);
	}

	/**
	 * donne la limite inferieure du noeud ( la limite sur la valeur de
	 * segmentation choisie pour ce noeud )
	 */
	private double getThreshold() {
		return m_threshold;
	}

	/**
	 * Donne la variance des samples
	 */
	protected double getSamplesVariance() {
		/**
		 * http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
		 */
		double mean = 0;
		double S = 0;
		long sumweight = m_samplesSummary[0];
		int i;
		for (i = 1; i < m_nbSamples; i++) {
			long weight = m_samplesSummary[i];
			long temp = weight + sumweight;
			if (weight != 0) {
				S = S + sumweight * weight * (i - mean) * (i - mean) / temp;
				mean = mean + (i - mean) * weight / temp;
			}
			sumweight = temp;
		}
		double variance = S * i / ((i - 1) * sumweight);
		return variance;
	}

	public int getSegmentingInfoIndex() {
		return m_segmentingInfoIndex;
	}

	@Override
	public int compareTo(Node o) {
		if (m_threshold > o.m_threshold) {
			return -1;
		}
		if (m_threshold < o.m_threshold) {
			return 1;
		}
		return 0;
	}

	public int paint(Hud hud, int offset) {
		int i = 0;
		for (Node node : m_childNodes) {
			i += Math.max(node.paint(hud, offset + i) - 1, 0);
			i++;
		}
		Node parent = m_parent;
		int parentCount = 0;
		while (parent != null) {
			parent = parent.m_parent;
			parentCount++;
		}
		String ch = "X";
		if (m_childLeafs.size() > 0)
			ch = "L";
		else if (m_childNodes.size() > 1)
			ch = Integer.toString(m_segmentingInfoIndex);
		hud.drawString(ch, 790 - offset * 10, 10 + parentCount * 10);
		return i;
	}

	/**
	 * @return
	 */
	public long[] getSamples() {
		return m_samplesSummary;
	}

	/**
	 * @return
	 */
	public double[] getSamplesHitChances() {
		// long pic = m_samplesSummary[(int)getSamplesPic()];
		long pic = 1;
		for (int i = 0; i < m_samplesSummary.length; i++) {
			if (m_samplesSummary[i] > pic)
				pic = m_samplesSummary[i];
		}
		double[] hitchances = new double[m_nbSamples];
		for (int i = 0; i < m_samplesSummary.length; i++) {
			hitchances[i] = (double) m_samplesSummary[i] / (double) pic;
		}
		return hitchances;
	}

	/**
	 * pour le debug affiche le chemin choisi dans l'arbre pour la solution de
	 * tir
	 * 
	 * @param si
	 * @return
	 */
	public Vector<String> getSolutionString(SegmentationInfo si) {
		String txt;

		// si le noeud contiens des feuilles
		if (!m_childLeafs.isEmpty()) {
			Vector<String> v = new Vector<String>();
			v.add("L");
			return v;
		} else if (!m_childNodes.isEmpty()) {
			double infoValue = si.getInfo(m_segmentingInfoIndex);
			// parcours des nodes filles
			txt = "[";
			for (Node node : m_childNodes) {
				if (node.getThreshold() == Double.NEGATIVE_INFINITY)
					txt += "-inf";
				else
					txt += ""
							+ (String.format("%.2f", node.getThreshold()))
									.toString() + ":";
			}
			txt += "]";
			for (Node node : m_childNodes) {
				if (infoValue >= node.getThreshold()) {
					String strInfoValue = String.format("%.2f", infoValue)
							.toString();
					String strThreshold = String.format("%.2f",
							node.getThreshold()).toString();
					txt = ""
							+ SegmentationInfo
									.getDimensionName(m_segmentingInfoIndex)
							+ txt + "(" + strInfoValue + ">=" + strThreshold
							+ ")>" + "\n\t";
					Vector<String> v = node.getSolutionString(si);
					v.add(0, txt);
					return v;
				}
			}
		}

		// aucunne solution trouvee ...
		Vector<String> v = new Vector<String>();
		v.add("pas de solution de tir");
		return v;
	}

}
