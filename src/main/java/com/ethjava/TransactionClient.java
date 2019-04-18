package com.ethjava;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import com.ethjava.utils.Environment;

public class TransactionClient {
	private static Web3j web3j;
	private static Admin admin;
	
//	private static String fromAddress = "0x7b1cc408fcb2de1d510c1bf46a329e9027db4112";
//	private static String toAddress = "0x05f50cd5a97d9b3fec35df3d0c6c8234e6793bdf";
	private static String fromAddress = "0x8fb40485f0b852b626b3b24ebff9c78500b1df71";
	private static String toAddress = "0xde7edd81450c57e13a7e5312c2ded96c39712dfd";
//	private static String toAddress = "0x379ee7CE65951832B09EACFFD95437b1350aD508";
	private static BigDecimal defaultGasPrice = BigDecimal.valueOf(5);
	

	public static void main(String[] args) throws Exception{
		web3j = Web3j.build(new HttpService(Environment.RPC_URL));
		admin = Admin.build(new HttpService(Environment.RPC_URL));

//		getBalance(fromAddress);
//		sendTransaction();
		
		
		/**
		 *  1.마이너들이 블록에 담을 트랜잭션들을 TxPool 에서 가져온다.
			2.트랜잭션 들을 각 주소 별 Nonce(순서)와 GasPrice로 순서를 정한다.
			3.마이너들이 트랜잭션에 설정된 값 (Gas Limit * Gas Price)만큼을 발신 주소로부터 가스로 가져온다.
			4.트랜잭션을 실행하며 명령어에 따라 가스를 소모한다. Gas Limit보다 남는 가스는 다시 돌려주지만 가스가 모자랄 경우 돌려주지 않고 실행했던 것을 되돌린다.
			5.실행된 트랜잭션 들을 블록에 담는다. 이때 이 트랜잭션들의 개별 Gas Limit의 총합은 Block Gas Limit을 넘을 수 없다.
		 */
		BigDecimal amount = new BigDecimal("100000000000000");
		BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
		Transaction transaction = makeTransaction(fromAddress, toAddress,null, null, null, value);
		
		//21000 Gas는 21000gwei 이고, 즉 0.000000021 이더이다. 
		//1 wei = 0.1¹⁸ ether
		//1 gwei = 10⁹ wei = 0.1⁹ ether
		BigInteger gasLimit = getTransactionGasLimit(transaction);
		System.out.println("gasLimit = "  + gasLimit); 
		BigInteger gasPrice = Convert.toWei(defaultGasPrice, Convert.Unit.GWEI).toBigInteger();
		System.out.println("gasPrice = "  + gasPrice); 
		System.out.println("gasLimit * gasPrice = " + gasLimit.multiply(gasPrice));
		
	}

	/**
	 * 获取余额
	 *
	 * @param address 钱包地址
	 * @return 余额
	 */
	private static BigInteger getBalance(String address) {
		BigInteger balance = null;
		try {
			EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
			balance = ethGetBalance.getBalance();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("address " + address + " balance " + balance + "wei");
		return balance;
	}

	/**
	 * 生成一个普通交易对象
	 *
	 * @param fromAddress 放款方
	 * @param toAddress   收款方
	 * @param nonce       交易序号
	 * @param gasPrice    gas 价格
	 * @param gasLimit    gas 数量
	 * @param value       金额
	 * @return 交易对象
	 */
	private static Transaction makeTransaction(String fromAddress, String toAddress,
											   BigInteger nonce, BigInteger gasPrice,
											   BigInteger gasLimit, BigInteger value) {
		Transaction transaction;
		
		transaction = Transaction.createEtherTransaction(fromAddress, nonce, gasPrice, gasLimit, toAddress, value);
		return transaction;
	}

	/**
	 * 获取普通交易的gas上限
	 *
	 * @param transaction 交易对象
	 * @return gas 上限
	 */
	private static BigInteger getTransactionGasLimit(Transaction transaction) {
		BigInteger gasLimit = BigInteger.ZERO;
		try {
			EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
			gasLimit = ethEstimateGas.getAmountUsed();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return gasLimit;
	}

	/**
	 * 获取账号交易次数 nonce
	 *
	 * @param address 钱包地址
	 * @return nonce
	 */
	private static BigInteger getTransactionNonce(String address) {
		BigInteger nonce = BigInteger.ZERO;
		try {
			EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send();
			nonce = ethGetTransactionCount.getTransactionCount();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nonce;
	}

	/**
	 * 发送一个普通交易
	 *
	 * @return 交易 Hash
	 */
	private static String sendTransaction() {
		String password = "111222333";
//		String password = "qqqwwweee";
		BigInteger unlockDuration = BigInteger.valueOf(60L);
		BigDecimal amount = new BigDecimal("100");
		String txHash = null;
		try {
			PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(fromAddress, password, unlockDuration).send();

			if (personalUnlockAccount.accountUnlocked()) {
				BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
				Transaction transaction = makeTransaction(fromAddress, toAddress,
						null, null, null, value);
				//不是必须的 可以使用默认值
				BigInteger gasLimit = getTransactionGasLimit(transaction);
				//不是必须的 缺省值就是正确的值
				BigInteger nonce = getTransactionNonce(fromAddress);
//				BigInteger nonce = getTransactionNonce(toAddress);
				//该值为大部分矿工可接受的gasPrice
				BigInteger gasPrice = Convert.toWei(defaultGasPrice, Convert.Unit.GWEI).toBigInteger();
				transaction = makeTransaction(fromAddress, toAddress,
//				transaction = makeTransaction(toAddress, fromAddress,
						nonce, gasPrice,
						gasLimit, value);
				EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).send();
				txHash = ethSendTransaction.getTransactionHash();
				
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("tx hash " + txHash);
		return txHash;
	}
	
}
