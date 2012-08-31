package com.appspresso.waikiki.filesystem;

import com.appspresso.api.AxError;
import com.appspresso.waikiki.filesystem.errors.IOError;
import com.appspresso.waikiki.filesystem.errors.InvalidValuesError;
import com.appspresso.waikiki.filesystem.errors.NotFoundError;

public interface WkDirectory {
    /**
     * @param dirPath 생성할 디렉토리의 현재 디렉토리에 대한 상대 경로
     * @return 생성한 디렉토리의 WkFileHandle
     * @throws InvalidValuesError 유효한 경로가 아님
     * @throws IOError 해당 파일이 이미 존재
     * @throws UnknownError 그 밖에 다른 이유로 디렉토리 생성에 실패했을 때 발생
     */
    public WkFileHandle createDirectory(String dirPath) throws InvalidValuesError, IOError,
            UnknownError;

    /**
     * @param filePath 생성할 파일의 현재 디렉토리에 대한 상대 경로
     * @return 생성한 파일의 WkFileHandle
     * @throws InvalidValuesError 유효한 경로가 아님
     * @throws IOError 해당 파일이 이미 존재
     * @throws UnknownError 그 밖에 다른 이유로 파일 생성에 실패했을 때 발생
     */
    public WkFileHandle createFile(String filePath) throws InvalidValuesError, IOError,
            UnknownError;

    /**
     * 해당 경로에 대한 디렉토리를 삭제한다. 삭제할 디렉토리는 반드시 현재 디렉토리의 하위 디렉토리이어야 한다.
     * 
     * @param dirPath 삭제할 디렉토리의 전체 가상 경로
     * @param recursive 디렉토리가 비어있지 않을 때 재귀적으로 모두 삭제할 것인지 여부
     * @throws NotFoundError 삭제할 디렉토리가 존재하지 않음
     * @throws IOError 이 디렉토리가 읽기 전용으로 하위 디렉토리를 삭제할 수 없거나 recursive가 fales인데 빈 디렉토리가 아님
     * @throws AxError 그 밖에 다른 이유로 디렉토리를 삭제하는데 실패했을 때 발생
     */
    public void deleteDirectory(String dirPath, boolean recursive) throws NotFoundError, IOError,
            UnknownError;

    /**
     * 해당 경로에 대한 파일을 삭제한다. 삭제할 파일은 반드시 현재 디렉토리의 하위 파이어야 한다.
     * 
     * @param filePath 삭제할 파일의 전체 가상 경
     * @throws NotFoundError 삭제할 파일이 존재하지 않음
     * @throws IOError 이 디렉토리가 읽기 전용으로 하위 파일을 삭제할 수 없음.
     * @throws AxError 알 수 없는 이유로 파일을 삭제하는데 실패했을 때 발생
     */
    public void deleteFile(String filePath) throws NotFoundError, IOError, UnknownError;

    /**
     * 해당 경로의 파일에 대한 WkFileHandle를 반환한다.
     * 
     * @param filePath 현재 디렉토리에 대해 상대적인 파일(혹은 디렉토리)의 경로
     * @return WkFileHandle
     * @throws InvalidValuesError 파일 경로가 유효하지 않음
     * @throws NotFoundError 파일 경로에 해당하는 파일이 존재하지 않음
     * @throws UnknownError 알 수 없는 이유로 WkFileHandle을 반환하는데 실패했을 때 발생
     */
    public WkFileHandle resolve(String filePath) throws InvalidValuesError, NotFoundError,
            UnknownError;

    /**
     * 현재 디렉토리의 하위 파일 목록을 반환한다. WkFileFilter가 null이 아니라면 이 파일 필터에 대응되는 목록만 반환된다.
     * 
     * @param filter WkFileFilter
     * @return 현재 디렉토리의 하위 파일 목록
     * @throws AxError
     */
    public WkFileHandle[] listFiles(WkFileFilter filter) throws AxError;

    /**
     * 현재 디렉토리가 루트 디렉토리인지 반한한다.
     * 
     * @return 현재 디렉토리가 파일시스템의 루트이면 true, 그렇지 않으면 false
     */
    public boolean isRoot();
}
