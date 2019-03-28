package com.alibaba.fescar.samples.tcc.transfer.activity.impl;


import com.alibaba.fescar.samples.tcc.transfer.action.FirstTccAction;
import com.alibaba.fescar.samples.tcc.transfer.action.SecondTccAction;
import com.alibaba.fescar.samples.tcc.transfer.activity.TransferService;
import com.alibaba.fescar.spring.annotation.GlobalTransactional;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 转账服务实现
 *
 * @author zhangsen
 */
public class TransferServiceImpl implements TransferService {

    private FirstTccAction firstTccAction;

    private SecondTccAction secondTccAction;

    /**
     * 转账操作
     * @param from  扣钱账户
     * @param to  加钱账户
     * @param amount  转账金额
     * @return
     */
    @Override
    @GlobalTransactional
    public boolean transfer(final String from, final String to, final double amount) {
        //扣钱参与者，一阶段执行
        boolean ret = firstTccAction.prepare_minus(null, from, amount);

        if(!ret){
            //扣钱参与者，一阶段失败; 回滚本地事务和分布式事务
            throw new RuntimeException("账号:["+from+"] 预扣款失败");
        }

        //加钱参与者，一阶段执行
        ret = secondTccAction.prepare_add(null, to, amount);

        if(!ret){
            throw new RuntimeException("账号:["+to+"] 预收款失败");
        }

        System.out.println(String.format("transfer amount[%s] from [%s] to [%s] finish.", String.valueOf(amount), from, to));
        return true;
    }

    public void setFirstTccAction(FirstTccAction firstTccAction) {
        this.firstTccAction = firstTccAction;
    }

    public void setSecondTccAction(SecondTccAction secondTccAction) {
        this.secondTccAction = secondTccAction;
    }
}
