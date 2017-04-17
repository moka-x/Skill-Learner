import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnMore{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Category category = new Category();
		//final String inputFileName = "sample.txt";
		final String inputFileName = args[0];
		// result output into disassembly.txt
		final String outFileName1 = "disassembly.txt";
		final String outFileName2 = "simulation.txt";
		final int startAddr = 128;
		Map<String,List<?>> mapList = BinarytoInstr(inputFileName,category);
		List<String> disassemblyList = (List<String>) mapList.get("disassemblyList");
		
		List<String> simulationList = execInstruction(mapList,startAddr);
		try {
			if(createFile(new File(outFileName1))){
				writeFile(disassemblyList, outFileName1);
				System.out.println("disassembly.txt output completed, the path is: " + new File(outFileName1).getCanonicalPath());
				}
			} catch (Exception e) {
				e.printStackTrace();
		}
		// execution processes output into simulation.txt
		try {
			if(createFile(new File(outFileName2))){
				writeFile(simulationList, outFileName2);
				System.out.println("simulation.txt output completed, the path is: " + new File(outFileName2).getCanonicalPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
}
	//MIPS Category instruction format
	public static class Category{
		private Map<String, String> categorySet;
		private Map<String, String> c1;
		private Map<String, String> c2;
		private Map<String, String> c3;
		public Category(){	
			Map<String,String> CategorySet = new HashMap<String,String>();
			Map<String,String> C1 = new HashMap<String,String>();
			Map<String,String> C2 = new HashMap<String,String>();
			Map<String,String> C3 = new HashMap<String,String>();
			CategorySet.put("Category1", "000");
			CategorySet.put("Category2", "110");
			CategorySet.put("Category3", "111");
			C1.put("J","000");
			C1.put("BEQ","010");
			C1.put("BGTZ","100");
			C1.put("BREAK","101");
			C1.put("SW","110");
			C1.put("LW","111");
			C2.put("ADD","000");
			C2.put("SUB","001");
			C2.put("MUL","010");
			C2.put("AND","011");
			C2.put("OR","100");
			C2.put("XOR","101");
			C2.put("NOR","110");
			C3.put("ADDI","000");
			C3.put("ANDI","001");
			C3.put("ORI","010");
			C3.put("XORI","011");
			setC1(C1);
			setC2(C2);
			setC3(C3);
			setCategorySet(CategorySet);		
		}
		public Map<String, String> getCategorySet() {
			return categorySet;
		}
		public void setCategorySet(Map<String, String> categorySet) {
			this.categorySet = categorySet;
		}
		public Map<String, String> getC1() {
			return c1;
		}
		public void setC1(Map<String, String> c1) {
			this.c1 = c1;
		}
		public Map<String, String> getC2() {
			return c2;
		}
		public void setC2(Map<String, String> c2) {
			this.c2 = c2;
		}
		public Map<String, String> getC3() {
			return c3;
		}
		public void setC3(Map<String, String> c3) {
			this.c3 = c3;
		}
	}
	//Disassemble Instruction class
	public static class Disassembler{
		
		private String binary;
		private String disassembl;
		private String instruction;
		private int address;
		private int rd;
		private int rs;
		private int rt;
		private int immediate;
		private int offset;
		private int base;
		private int target;
		public String getBinary() {
			return binary;
		}
		public void setBinary(String binary) {
			this.binary = binary;
		}
		public String getDisassembl() {
			return disassembl;
		}
		public void setDisassembl(String disassembl) {
			this.disassembl = disassembl;
		}
		public String getInstruction() {
			return instruction;
		}
		public void setInstruction(String instruction) {
			this.instruction = instruction;
		}
		public int getAddress() {
			return address;
		}
		public void setAddress(int address) {
			this.address = address;
		}
		public int getRd() {
			return rd;
		}
		public void setRd(int rd) {
			this.rd = rd;
		}
		public int getRs() {
			return rs;
		}
		public void setRs(int rs) {
			this.rs = rs;
		}
		public int getRt() {
			return rt;
		}
		public void setRt(int rt) {
			this.rt = rt;
		}
		public int getImmediate() {
			return immediate;
		}
		public void setImmediate(int immediate) {
			this.immediate = immediate;
		}
		public int getOffset() {
			return offset;
		}
		public void setOffset(int offset) {
			this.offset = offset;
		}
		public int getBase() {
			return base;
		}
		public void setBase(int base) {
			this.base = base;
		}
		public int getTarget() {
			return target;
		}
		public void setTarget(int target) {
			this.target = target;
		}
	}
	//Read the binary txt, translate into assemble instruction
	public static Map<String,List<?>> BinarytoInstr(String inputFileName,Category category){
		final int startAddr = 128; 
		boolean isBreak = false;
		int address = startAddr; 
		Map<String,List<?>> MapList = new HashMap<String,List<?>>();
		List<String> disassemblyList = new ArrayList<String>(); 
		List<Integer> dataList = new ArrayList<Integer>(); 
		List<Disassembler> disassemblers = new ArrayList<Disassembler>();
		List<String> instructionList = readFileByChars(inputFileName);
		List<Integer> registerValueList = new ArrayList<Integer>();
		for(int k = 0; k < 32; k++){
			registerValueList.add(0);
		}
		for(int i = 0; i < instructionList.size(); i++){
			String instruction = instructionList.get(i);
			String out = "";
			if (!isBreak) {
				Disassembler disassemb = new Disassembler();
				String categoryType = null;
				String temp = "";
				String opcode = null;
				String first3Bits = instruction.substring(0, 3); 
				String offsetStr = instruction.substring(instruction.length() - 16, instruction.length());
				String immediateStr = instruction.substring(instruction.length() - 16, instruction.length());
				int immediate = calculate(immediateStr);
				int offset = Integer.parseInt(offsetStr, 2);
				int base = Integer.parseInt(instruction.substring(6, 11), 2);
				int rs = 0,rt = 0,rd = 0;			
				if(first3Bits.equals(category.categorySet.get("Category1"))){
					opcode = instruction.substring(3, 6);
					if(opcode.equals(category.c1.get("J"))){
						String targetStr = instruction.substring(instruction.length() - 26, instruction.length());
						int target = Integer.parseInt(targetStr + "00", 2);
						categoryType="J";
						temp="J" + " #"+target;
						disassemb.setTarget(target);
					}else if(opcode.equals(category.c1.get("BEQ"))){
						rs = Integer.parseInt(instruction.substring(6, 11), 2);
						rt = Integer.parseInt(instruction.substring(11, 16), 2);
						offset = calculate(offsetStr + "00");
						categoryType="BEQ";
						temp = "BEQ" + " R" + rs + "," + " R" + rt + "," + " #" + offset;
						disassemb.setOffset(offset);
						disassemb.setRs(rs);
						disassemb.setRt(rt);
					}else if(opcode.equals(category.c1.get("BGTZ"))){
						rs = Integer.parseInt(instruction.substring(6, 11), 2);
						offset = calculate(offsetStr + "00");
						categoryType="BGTZ";
						temp = "BGTZ" + " R" + rs + "," + " #" + offset;
						disassemb.setOffset(offset);
						disassemb.setRs(rs);
					}else if(opcode.equals(category.c1.get("BREAK"))){
						temp = "BREAK";
						categoryType = "BREAK";
						isBreak = true;
					}else if(opcode.equals(category.c1.get("SW"))){
						categoryType="SW";
						rt = Integer.parseInt(instruction.substring(11, 16), 2);
						temp = "SW" + " R" + rt + "," + " " + offset + "(R" + base + ")";
						disassemb.setOffset(offset);
						disassemb.setBase(base);
						disassemb.setRt(rt);
					}else if(opcode.equals(category.c1.get("LW"))){
						categoryType="LW";
						rt = Integer.parseInt(instruction.substring(11, 16), 2);
						temp = "LW" + " R" + rt + "," + " " + offset + "(R" + base + ")";
						disassemb.setOffset(offset);
						disassemb.setBase(base);
						disassemb.setRt(rt);
					}
				}else if(first3Bits.equals(category.categorySet.get("Category2"))){
					opcode = instruction.substring(13, 16);
					rs = Integer.parseInt(instruction.substring(3, 8), 2);
					rt = Integer.parseInt(instruction.substring(8, 13), 2);
					rd = Integer.parseInt(instruction.substring(16, 21), 2);
					if(opcode.equals(category.c2.get("ADD"))){
						categoryType="ADD";
					}else if(opcode.equals(category.c2.get("SUB"))){
						categoryType="SUB";
					}else if(opcode.equals(category.c2.get("MUL"))){
						categoryType="MUL";
					}else if(opcode.equals(category.c2.get("AND"))){
						categoryType="AND";
					}else if(opcode.equals(category.c2.get("OR"))){
						categoryType="OR";
					}else if(opcode.equals(category.c2.get("XOR"))){
						categoryType="XOR";
					}else if(opcode.equals(category.c2.get("NOR"))){
						categoryType="NOR";
					}
					temp = categoryType + " R" + rd + "," + " R" + rs + "," + " R" + rt;
					disassemb.setRd(rd);
					disassemb.setRs(rs);
					disassemb.setRt(rt);
				}else if(first3Bits.equals(category.categorySet.get("Category3"))){
					opcode = instruction.substring(13, 16);
					rs = Integer.parseInt(instruction.substring(3, 8), 2);
					rt = Integer.parseInt(instruction.substring(8, 13), 2);
					if(opcode.equals(category.c3.get("ADDI"))){
						categoryType="ADDI";
					}else if(opcode.equals(category.c3.get("ANDI"))){
						categoryType="ANDI";
					}else if(opcode.equals(category.c3.get("ORI"))){
						categoryType="ORI";
					}else if(opcode.equals(category.c3.get("XORI"))){
						categoryType="XORI";
					}
					temp = categoryType + " R" + rt + "," + " R" + rs + "," + " #" + immediate;
					disassemb.setRt(rt);
					disassemb.setRs(rs);
					disassemb.setImmediate(immediate);
				}
				disassemb.setDisassembl(temp);
				out += instruction + "\t" + address + "\t" +temp;
				disassemb.setBinary(instruction);
				disassemb.setAddress(address);
				disassemb.setInstruction(categoryType);
				disassemblers.add(disassemb);
				disassemblyList.add(out);
			}else{
				out = "";
				String complement = toComplement(instruction);
				int result = calculate(complement);
				out = instruction + "\t" + address + "\t" + result;
				disassemblyList.add(out);			
				dataList.add(result);
			}
			address += 4;
		}
		MapList.put("disassemblyList",disassemblyList);
		MapList.put("dataList",dataList);
		MapList.put("disassemblers",disassemblers);
		MapList.put("registerValueList",registerValueList);
		return MapList;
	}
	//calculate the immediate 
	public static int calculate(String str) {
		if (null == str || str.isEmpty()) {
			return 0;
		}
		char ch = str.charAt(0);
		str = str.substring(1, str.length());
		int result = Integer.parseInt(str, 2);
		if (ch == '1') {
			result *= (-1);
		}
		return result;
	}
	//Binary data into complement type
	public static String toComplement(String str) {

		if (null == str || str.isEmpty()) {
			return null;
		}
		Integer num;
		for (int i = 0; i < str.length(); i++) {
			num = str.charAt(i) - '0';
			if (num > 1 || num < 0) {
				return null;
			}
		}
		char ch = str.charAt(0);
		if (ch == '0') { 
			return str;
		} else {
			StringBuilder sb = new StringBuilder("");
			for (int i = 1; i < str.length(); i++) {
				if (str.charAt(i) == '0') {
					sb.append('1');
				} else {
					sb.append('0');
				}
			}
			int result = Integer.parseInt(sb.toString(), 2); 
			result++;
			String resultStr = String.valueOf(Integer.toBinaryString(result)); 
			String newStr = "";
			newStr += ch;
			for (int j = 1; j < 32 - resultStr.length(); j++) {
				newStr += "0";
			}
			newStr += resultStr;
			return newStr;
		}
	}
	//read binary txt
	public static List<String> readFileByChars(String fileName) {
		List<String> list = new ArrayList<String>();
		File file = new File(fileName);
		Reader reader = null;
		try {		
			reader = new InputStreamReader(new FileInputStream(file));
			int tempchar;
			int i = 0;
			StringBuffer buf = new StringBuffer();
			while ((tempchar = reader.read()) != -1) {			
				if (((char) tempchar) == '0' || ((char) tempchar) == '1') {
					buf.append((char) tempchar);
					i++;
				}
				//every 32 bit is a MIPS instruction
				if (i == 32) {
					list.add(buf.toString());
					buf = new StringBuffer();
					i = 0;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	//create txt file
	public static boolean createFile(File fileName) throws Exception {
		boolean flag = false;
		try {
			if (!fileName.exists()) {
				fileName.createNewFile();
				flag = true;
			}else{
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	//write into the txt file
	public static boolean writeFile(List<String> newStrList, String fileName)
			throws IOException {
		boolean flag = false;

		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			File file = new File(fileName);
			StringBuffer buf = new StringBuffer();
			for(String str : newStrList){
				buf.append(str);
				buf.append("\r\n");
			}

			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buf.toString().toCharArray());
			pw.flush();

			flag = true;
		} catch (IOException e1) {
			throw e1;
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return flag;
	}

	//execute the assemble instruction
	@SuppressWarnings("unchecked")
	public static List<String> execInstruction(Map<String,List<?>> MapList,int startAddr){
		
		List<String> simulationList = new ArrayList<String>();
		int cycleCount = 0;
		boolean isBreak = false;
		List<Integer> dataList = (List<Integer>) MapList.get("dataList"); 
		List<Disassembler> disassemblers = (List<Disassembler>) MapList.get("disassemblers");
		List<Integer> registerValueList = (List<Integer>) MapList.get("registerValueList"); 
		int dataStartAddr = startAddr + disassemblers.size()*4;
		for(int i = 0; i < disassemblers.size(); i++){
			cycleCount++;
			Disassembler disassemb = disassemblers.get(i);
			String cycleOut = "";
			String instruction = disassemb.getInstruction();
			String disassembl = disassemb.getDisassembl();
			int rd = disassemb.getRd();
			int rs = disassemb.getRs();
			int rt = disassemb.getRt();
			int offset = disassemb.getOffset();
			int immediate = disassemb.getImmediate();
			int address = disassemb.getAddress();
			int target = disassemb.getTarget();
			int base = disassemb.getBase();
			cycleOut += "Cycle:" + cycleCount + "\t" + address + "\t" + disassembl + "\r\n\r\n";
			switch(instruction){
				case "J":{
					i = (target - startAddr) / 4; 
					i--;
				} break;
				case "BEQ":{
					if(registerValueList.get(rs) == registerValueList.get(rt)){
						i = ((address + 4) + offset - startAddr) / 4; 
						i--;
					}
				} break;
				case "BGTZ":{
					if(registerValueList.get(rs) > 0){
						i = ((address + 4) + offset - startAddr) / 4; 
						i--; 
					}
				} break;
				case "BREAK":{
					isBreak = true;
				} break;
				case "SW":{
					int index = (offset + registerValueList.get(base) - dataStartAddr) / 4;
					dataList.set(index, registerValueList.get(rt));
				} break;
				case "LW":{
					int index = (offset + registerValueList.get(base) - dataStartAddr) / 4;
					registerValueList.set(rt, dataList.get(index));
				} break;
				case "ADD":{
					registerValueList.set(rd, registerValueList.get(rs) + registerValueList.get(rt));
				} break;
				case "SUB":{
					registerValueList.set(rd, registerValueList.get(rs) - registerValueList.get(rt));
				} break;
				case "MUL":{
					registerValueList.set(rd, registerValueList.get(rs) * registerValueList.get(rt));
				} break;
				case "AND":{
					registerValueList.set(rd, registerValueList.get(rs) & registerValueList.get(rt));
				} break;
				case "OR":{
					registerValueList.set(rd, registerValueList.get(rs) | registerValueList.get(rt));
				} break;
				case "XOR":{
					registerValueList.set(rd, registerValueList.get(rs) ^ registerValueList.get(rt));
				} break;
				case "NOR":{
					registerValueList.set(rd, registerValueList.get(rs) ^ registerValueList.get(rt));
				} break;
				case "ADDI":{
					registerValueList.set(rt, registerValueList.get(rs) + immediate);
				} break;
				case "ANDI":{
					registerValueList.set(rt, registerValueList.get(rs) + immediate);
				} break;
				case "ORI":{
					registerValueList.set(rt, registerValueList.get(rs) | immediate);
				} break;
				case "XORI":{
					registerValueList.set(rt, registerValueList.get(rs) ^ immediate);
				} break;
				
			}
			cycleOut += "Registers" + "\r\n" + "R00:";
			int j = 0;
			for(Integer val : registerValueList){
				cycleOut += "\t" + val;
				if((j+1) % 8==0&j+1<32){
					cycleOut += "\r\n" + "R";
					if(j+1==8){
						cycleOut += "0"+(j+1)+ ":";
					}else{
						cycleOut += (j+1)+ ":";
					}	
				}
				j++;
			}
			cycleOut += "\r\n\r\n" + "Data";

			String dataStr = "";
			int dataAddr = dataStartAddr;
			for(int k = 0; k < dataList.size(); k++){			
				if(k % 8 ==0){
					dataStr += "\r\n" + dataAddr + ":";
				}
				dataStr += "\t" + dataList.get(k);
				dataAddr += 4;
			}
			cycleOut += dataStr + "\r\n" +"-----------------------------------------------------------------"+ "\r\n";
			simulationList.add(cycleOut);
			
			if(isBreak){ 
				break;
			}
		}
		
		return simulationList;
	}
}