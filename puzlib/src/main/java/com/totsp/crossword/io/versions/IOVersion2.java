package com.totsp.crossword.io.versions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import com.totsp.crossword.io.IO;
import com.totsp.crossword.puz.Box;
import com.totsp.crossword.puz.Puzzle;
import com.totsp.crossword.puz.PuzzleMeta;

public class IOVersion2 extends IOVersion1 {

	@Override 
	protected void applyMeta(Puzzle puz, PuzzleMeta meta){
		super.applyMeta(puz, meta);
		//System.out.println("Applying V2 Meta");
		puz.setUpdatable(meta.updateable);
		puz.setSourceUrl(meta.sourceUrl);
	}
	
	@Override
	public PuzzleMeta readMeta(InputStream is) throws IOException{
		//System.out.println("Read V2");
		PuzzleMeta meta = new PuzzleMeta();
		DataInputStream dis = is instanceof DataInputStream ? (DataInputStream) is : new DataInputStream(is);
		meta.author = IO.readNullTerminatedString(dis);
		meta.source = IO.readNullTerminatedString(dis);
		meta.title = IO.readNullTerminatedString(dis);
		meta.date = new Date( dis.readLong() );
		meta.percentComplete = dis.readInt();
		meta.updateable = is.read() == 1;
		meta.sourceUrl = IO.readNullTerminatedString(is);
		//System.out.println(meta);
		return meta;
	}
	
	@Override 
	public void write(Puzzle puz, OutputStream os) throws IOException {
		DataOutputStream dos = os instanceof DataOutputStream ? (DataOutputStream) os : new DataOutputStream(os);
		IO.writeNullTerminatedString(dos, puz.getAuthor());
		IO.writeNullTerminatedString(dos, puz.getSource());
		IO.writeNullTerminatedString(dos, puz.getTitle());
		dos.writeLong(puz.getDate() == null ? 0 : puz.getDate().getTime());
		dos.writeInt(puz.getPercentComplete());
		dos.write(puz.isUpdatable() ? 1 : -1); 
		IO.writeNullTerminatedString(dos, puz.getSourceUrl());
		//System.out.println("Meta written.");
		Box[][] boxes = puz.getBoxes();
		for(Box[] row : boxes ){
			for(Box b : row){
				if(b == null){
					continue;
				}
				dos.writeBoolean(b.isCheated());
				IO.writeNullTerminatedString(dos, b.getResponder());
			}
		}
		dos.writeLong(puz.getTime());
	}
	
}
