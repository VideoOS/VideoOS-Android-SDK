package cn.com.venvy.common.utils;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yanjiangbo on 2017/5/10.
 */

public class VenvyAsyncTaskUtil {

    private static ConcurrentHashMap<String, AsyncTask> asyncTaskMap = new ConcurrentHashMap<>();

    private static class CustomTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {
        final IAsyncCallback<Result> asyncCallback;
        final IDoAsyncTask<Param, Result> doAsyncTask;
        final String keyTask;
        private Exception ie = null;

        public CustomTask(String keyTask, IDoAsyncTask<Param, Result> doAsyncTask, IAsyncCallback<Result> asyncCallback) {
            this.asyncCallback = asyncCallback;
            this.doAsyncTask = doAsyncTask;
            this.keyTask = keyTask;
        }

        @Override
        protected Result doInBackground(Param[] params) {
            if (isCancelled()) {
                return null;
            }
            if (doAsyncTask == null) {
                return null;
            }
            Result result = null;
            try {
                result = doAsyncTask.doAsyncTask(params);
            } catch (Exception ie) {
                this.ie = ie;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            asyncTaskMap.remove(keyTask);
            if (asyncCallback != null) {
                if (null == ie) {
                    asyncCallback.onPostExecute(result);
                } else {
                    asyncCallback.onException(ie);
                    ie = null;
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (asyncCallback != null) {
                asyncCallback.onPreExecute();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (asyncCallback != null) {
                asyncCallback.onCancelled();
            }
        }
    }

    public static <Param, Progress, Result> void doAsyncTask(final String keyTask,
                                                             final IDoAsyncTask<Param, Result> doAsyncTask,
                                                             final IAsyncCallback<Result> asyncCallback,
                                                             Param... param) {
        if (null == doAsyncTask) {
            VenvyLog.e("doAsyncTask can't be null");
            return;
        }
        if (TextUtils.isEmpty(keyTask)) {
            VenvyLog.e("keyTask can't be null");
            return;
        }
        AsyncTask<Param, Progress, Result> asyncTask = new CustomTask<>(keyTask, doAsyncTask, asyncCallback);

        cancel(keyTask);
        asyncTaskMap.put(String.valueOf(keyTask), asyncTask);
        if (asyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            asyncTask.execute(param);
        }
    }

    public static void cancel(String keyTask) {
        if (!TextUtils.isEmpty(keyTask) && asyncTaskMap.containsKey(keyTask)) {
            AsyncTask task = asyncTaskMap.get(keyTask);
            if (task != null) {
                task.cancel(true);
            }
            asyncTaskMap.remove(keyTask);
        }
    }

    public static void cancelAllTasks() {
        Set<Map.Entry<String, AsyncTask>> entrySet = asyncTaskMap.entrySet();
        Iterator<Map.Entry<String, AsyncTask>> it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry<String, AsyncTask> entry = it.next();
            AsyncTask task = entry.getValue();
            if (task != null) {
                task.cancel(true);
            }
            it.remove();
        }
        asyncTaskMap.clear();
    }

    public interface IAsyncCallback<Result> {
        void onPreExecute();

        void onPostExecute(Result result);

        void onCancelled();

        void onException(Exception ie);
    }

    public interface IDoAsyncTask<Param, Result> {
        Result doAsyncTask(Param... params) throws Exception;
    }

    public abstract static class CommonAsyncCallback<Result> implements IAsyncCallback<Result> {

        @Override
        public void onPreExecute() {

        }
    }

}
