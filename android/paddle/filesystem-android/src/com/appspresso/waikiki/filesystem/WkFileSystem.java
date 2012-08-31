package com.appspresso.waikiki.filesystem;

import com.appspresso.api.AxError;
import com.appspresso.api.fs.AxFileSystem;

public interface WkFileSystem extends AxFileSystem {

    /**
     * 이 파일시스템의 특정 위치에 해당 파일을 복사한다.
     * 
     * @param otherSystem 복사할 파일이 원래 위치하고 있는 WkFileSystem
     * @param originPath 복사할 파일의 완전한 가상 경로. 반드시 otherSystem에 속한 파일이어야 함
     * @param destPath 복사될 위치의 완전한 가상 경로. 반드시 이 FileSystem의 밑에 있어야 함
     * @param overwrite 복사될 위치에 이미 다른 파일이 존재할 경우 덮어쓰기 여부
     * @throws AxError 정상적으로 복사되지 않았을 경우 발생
     */
    public void copyToHere(WkFileSystem otherSystem, String originPath, String destPath,
            boolean overwrite) throws AxError;

    /**
     * 이 파일시스템의 특정 위치에 해당 파일을 이동한다. 일반적으로 같은 물리적 파일시스템상에 있을 경우 이름을 변경시키는 것과 같다. onSameMount를 적절히
     * 구현하여 서로 다른 물리적 파일시스템일 경우 파일을 복사 후 삭제하도록 한다.
     * 
     * @param otherSystem 이동할 파일이 원래 위치하고 있는 WkFileSystem
     * @param originPath 이동할 파일의 완전한 가상 경로. 반드시 otherSystem에 속한 파일이어야 함
     * @param destPath 이동될 위치의 완전한 가상 경로. 반드시 이 FileSystem의 밑에 있어야 함
     * @param overwrite 이동될 위치에 이미 다른 파일이 존재할 경우 덮어쓰기 여부
     * @throws AxError 정상적으로 이동되지 않았을 경우 발생
     */
    public void moveToHere(WkFileSystem otherSystem, String originPath, String destPath,
            boolean overwrite) throws AxError;

    /**
     * 해당 파일시스템과 이 파일시스템이 실제 물리적 파일시스템에서 같은 마운트인지 반환
     * 
     * @param fileSystem
     * @return 같은 마운트일 경우 true, 그렇지 않을 경우 false 반환
     */
    public boolean onSameMount(WkFileSystem fileSystem);
}
