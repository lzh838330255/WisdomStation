package com.winsion.dispatch.modules.grid.fragment.patrolplan;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.winsion.dispatch.application.AppApplication;
import com.winsion.dispatch.data.CacheDataSource;
import com.winsion.dispatch.data.NetDataSource;
import com.winsion.dispatch.data.constants.FieldKey;
import com.winsion.dispatch.data.constants.JoinKey;
import com.winsion.dispatch.data.constants.Mode;
import com.winsion.dispatch.data.constants.Urls;
import com.winsion.dispatch.data.constants.ViewName;
import com.winsion.dispatch.data.entity.OrderBy;
import com.winsion.dispatch.data.entity.ResponseForQueryData;
import com.winsion.dispatch.data.entity.WhereClause;
import com.winsion.dispatch.data.listener.ResponseListener;
import com.winsion.dispatch.modules.grid.entity.PatrolPlanEntity;
import com.winsion.dispatch.utils.ConvertUtils;
import com.winsion.dispatch.utils.JsonUtils;
import com.winsion.dispatch.utils.constants.Formatter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2017/12/26.
 * 巡检计划Presenter
 */

public class PatrolPlanPresenter implements PatrolPlanContract.Presenter {
    private PatrolPlanContract.View mView;
    private Context mContext;

    PatrolPlanPresenter(PatrolPlanContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {

    }

    @Override
    public void getPatrolPlanData() {
        if (AppApplication.TEST_MODE) {
            List<PatrolPlanEntity> testEntity = JsonUtils.getTestEntities(mContext, PatrolPlanEntity.class);
            mView.getPatrolPlanDataSuccess(testEntity);
            return;
        }
        List<WhereClause> whereClauses = new ArrayList<>();

        WhereClause whereClause = new WhereClause();
        whereClause.setFieldKey(FieldKey.EQUALS);
        whereClause.setFields("teamid");
        whereClause.setJoinKey(JoinKey.AND);
        whereClause.setValueKey(CacheDataSource.getTeamId());
        whereClauses.add(whereClause);

        WhereClause whereClause1 = new WhereClause();
        whereClause1.setFieldKey(FieldKey.EQUALS);
        whereClause1.setFields("createdate");
        whereClause1.setJoinKey(JoinKey.OTHER);
        whereClause1.setValueKey(ConvertUtils.formatDate(System.currentTimeMillis(), Formatter.DATE_FORMAT4));
        whereClauses.add(whereClause1);

        List<OrderBy> orderByList = new ArrayList<>();

        OrderBy orderBy = new OrderBy();
        orderBy.setField("planstarttime");
        orderBy.setMode(Mode.ASC);
        orderByList.add(orderBy);

        NetDataSource.post(this, Urls.BASE_QUERY, whereClauses, orderByList, ViewName.PATROL_INFO, 1,
                new ResponseListener<ResponseForQueryData<List<PatrolPlanEntity>>>() {
                    @Override
                    public ResponseForQueryData<List<PatrolPlanEntity>> convert(String jsonStr) {
                        Type type = new TypeReference<ResponseForQueryData<List<PatrolPlanEntity>>>() {
                        }.getType();
                        return JSON.parseObject(jsonStr, type);
                    }

                    @Override
                    public void onSuccess(ResponseForQueryData<List<PatrolPlanEntity>> result) {
                        mView.getPatrolPlanDataSuccess(result.getDataList());
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {
                        mView.getPatrolPlanDataFailed();
                    }
                });
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }
}
