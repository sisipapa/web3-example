package com.ethjava; 

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import com.ethjava.utils.Environment;




public class Web3jUtil {

	private static final Logger logger = LoggerFactory.getLogger(Web3jUtil.class);
	private static String status_err = "-100";
	private static String status_ok = "000";
	
	public static void main(String[] args) {
		String jsonParam = "{\"params\":\"0x3e80d1993db5991614818cc6ca0963849a5a11d4a8255dae5fe2ba79ae76df7e\",\"id\":\"wizbl\"}";
//		Web3jUtil.getTransactionByHash(jsonParam);
		Web3jUtil.getTransactionByHashReceipt(jsonParam);
	}
	
	public static Web3j init() {

		Web3j web3 = null;

		try {
			web3 = Web3j.build(new HttpService(Environment.RPC_URL));
		} catch (Exception e) {

			// TODO Auto-generated catch block
			StringBuilder sb = new StringBuilder();
			sb.append(e.toString());
			sb.append("\n");
			StackTraceElement element[] = e.getStackTrace();

			for (int idx = 0; idx < element.length; idx++){
				sb.append("\tat ");
				sb.append(element[idx].toString());
				sb.append("\n");
			}
			logger.error(sb.toString());
		}

		return web3;
	}

	public static boolean setShutdown(Web3j web3) {

		boolean bOK = false;
		try {
			web3.shutdown();
			bOK = true;
		} catch (Exception e) {

			// TODO Auto-generated catch block
			// TODO Auto-generated catch block
			StringBuilder sb = new StringBuilder();
			sb.append(e.toString());
			sb.append("\n");
			StackTraceElement element[] = e.getStackTrace();
			for (int idx = 0; idx < element.length; idx++){
				sb.append("\tat ");
				sb.append(element[idx].toString());
				sb.append("\n");
			}

			logger.error(sb.toString());
		}

		return bOK;

	}

	public static List<Map<String, Object>> getTransactionByHashReceipt(String json) throws JSONException {

		List<Map<String, Object>> objectList = null;
		Request<?, EthTransaction> hash = null;
		JSONObject jobj = new JSONObject(json);
		String msg = "처리 되었습니다.";
		int ieffected = -1;
		Web3j web3 = init();
		String params = "";

		if (json != null && json.contains("params") == true) {
			params = jobj.getString("params");
		}

		hash = web3.ethGetTransactionByHash(params);
		String value = "0";
		boolean bOK = false;

		try {
			EthTransaction eth = hash.send();
			Transaction transaction = eth.getTransaction().get();
			System.out.println("from------" + transaction.getFrom());// .from: Address- 20 Bytes - 보낸 사람의 주소입니다.
			System.out.println("to--------" + transaction.getTo());// .to: Address- 20 Bytes - 수신자의 주소. null언제 계약 생성 거래.
			System.out.println("value-----" + transaction.getValue());// .value: Quantity- 웨이에서 양도 된 가치.
			value = String.format("%s", transaction.getValue());
			bOK = true;
		} catch (IOException e) {

			msg = e.getMessage();
			logger.error(msg);
//			objectList = Util.SetMap("value", value, ieffected, status_err, msg);

		} catch (NoSuchElementException e) {

			// msg = Util.getPrintStacTraceString(e);
			msg = e.getMessage();
			logger.error(msg);
//			objectList = Util.SetMap("value", value, ieffected, status_err, msg);

		} catch (Exception e) {

			msg = e.getMessage();
			logger.error(msg);
//			objectList = Util.SetMap("value", value, ieffected, status_err, msg);

		}

		if (bOK == true) {

			Request<?, EthGetTransactionReceipt> receipt = null;
			receipt = web3.ethGetTransactionReceipt(params);

			try {

				EthGetTransactionReceipt eth = receipt.send();
				TransactionReceipt transaction = eth.getTransactionReceipt().get();
				System.out.println("transaction.getStatus()---->" + transaction.getStatus());
				transaction.getLogs();
				String status = null;
				int ieffect = 0;

				if (transaction.getStatus().equals("0x1") == true) {
					status = status_ok;
					ieffect = 1;
				} else {
					value = "0";
					status = status_err;
					ieffect = -1;
					msg = "값이 잘못되었습니다.";
				}

//				objectList = Util.SetMap("value", value, ieffect, status, msg);

			} catch (IOException e) {
				msg = e.getMessage();
				logger.error(msg);
//				objectList = Util.SetMap("txid", params, ieffected, status_err, msg);
			} catch (NoSuchElementException e) {
				// msg = Util.getPrintStacTraceString(e);
				msg = e.getMessage();
				logger.error(msg);
//				objectList = Util.SetMap("txid", params, ieffected, status_err, msg);
			} catch (Exception e) {
				msg = e.getMessage();
				logger.error(msg);
//				objectList = Util.SetMap("txid", params, ieffected, status_err, msg);
			}

		}

		return objectList;

	}

	public static Request<?, EthTransaction> getTransactionByHash(String json) throws JSONException {

		Request<?, EthTransaction> objectList = null;
		JSONObject jobj = new JSONObject(json);
		Web3j web3 = init();
		String params = "";
		String id = "";

		if (json != null && json.contains("params") == true) {
			params = jobj.getString("params");
			id = jobj.getString("id");
		}

		objectList = web3.ethGetTransactionByHash(params);

		try {

			EthTransaction eth = objectList.send();
			System.out.println("---->" + eth.getJsonrpc());
			System.out.println("---->" + eth.getTransaction());
			BigInteger index = BigInteger.ONE;
			Transaction transaction = eth.getTransaction().get();
			// assertThat(transaction.getBlockHash(), is(config.validBlockHash()));
			System.out.println("transaction.getBlockHash()---->" + transaction.getBlockHash());
			System.out.println("transaction.getTransactionIndex()---->" + transaction.getTransactionIndex());

			// System.out.println("------"+transaction.getHash() );//hash: Hash- 32 Bytes -
			// 트랜잭션의 해시.

			// System.out.println("------"+transaction.getNonce() );// nonce: Quantity- 이전에
			// 보낸 사람이 수행 한 트랜잭션의 수입니다.

			// System.out.println("------"+transaction.getBlockHash() );//: Hash- 32 Bytes
			// -이 트랜잭션이 있던 블록의 해시. null보류 중일 때.

			// System.out.println("------"+transaction.getBlockNumber();//: Quantity Tag-
			// null보류중인 트랜잭션이 있었던 블록 번호 .

			// System.out.println("------"+transaction.getTransactionIndex();//
			// .transactionIndex: Quantity- 블록 내의 트랜잭션 인덱스 위치의 정수. null보류 중일 때.

			System.out.println("from------" + transaction.getFrom());// .from: Address- 20 Bytes - 보낸 사람의 주소입니다.
			System.out.println("to--------" + transaction.getTo());// .to: Address- 20 Bytes - 수신자의 주소. null언제 계약 생성 거래.
			System.out.println("value-----" + transaction.getValue());// .value: Quantity- 웨이에서 양도 된 가치.

			// System.out.println("------"+transaction.getGasPrice();// .gasPrice: Quantity-
			// Wei에서 보낸 사람이 제공 한 가스 가격.

			// System.out.println("------"+transaction.getGas();// .gas: Quantity- 송부자가 제공 한
			// 가스.

			// System.out.println("------"+transaction.getInput();// .input: Data- 데이터가
			// 트랜잭션과 함께 전송됩니다.

			// System.out.println("------"+transaction.getV();// .v: Quantity- 서명의 표준화 된 V
			// 필드.

			// transaction.getst .standard_v: Quantity- 서명의 표준화 된 V 필드 (0 또는 1).

			// System.out.println("------"+transaction.getR();// .r: Quantity- 서명의 R 필드.

			// System.out.println("------"+transaction.getRaw();// .raw: Data- 원시 트랜잭션 데이터

			// System.out.println("------"+transaction.getPublicKey();// .publicKey: Hash-
			// 서명자의 공개 키.

			// transaction .networkId: Quantity- 트랜잭션의 네트워크 ID (있는 경우).

			// System.out.println("------"+transaction.getCreates();// .creates: Hash- 계약
			// 해시를 만듭니다.

			// transaction .condition: Object- (선택 사항) 조건부 제출, 블록 번호 block또는 타임 스탬프에 time나
			// null. (패리티 특성)

			// assertThat(transaction.getTransactionIndex(), equalTo(index));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Request<?, EthGetTransactionReceipt> receipt = null;
		receipt = web3.ethGetTransactionReceipt(params);

		try {
			EthGetTransactionReceipt eth = receipt.send();
			TransactionReceipt transaction = eth.getTransactionReceipt().get();
			System.out.println("transaction.getStatus()---->" + transaction.getStatus());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return objectList;

	}

}
