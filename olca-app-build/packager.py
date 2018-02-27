# This script creates the openLCA distribution packages. It currently only
# works on Windows as it calls native binaries to create the Windows installers
# (e.g. NSIS).

import datetime
import glob
import os
import subprocess
import shutil


def main():
    print('Create the distribution packages')
    if os.path.exists('packages'):
        shutil.rmtree('packages', ignore_errors=True)
        os.mkdir('packages')
    now = datetime.datetime.now()
    version_date = '%s_%d-%02d-%02d' % (get_version(), now.year,
                                        now.month, now.day)
    pack_linux(version_date)
    pack_macos(version_date)


def pack_linux(version_date):
    app_pack = glob.glob('builds/openlca_*-linux.gtk.x86_64.zip')
    if len(app_pack) == 0:
        print('ERROR: could not find Linux package')
        return
    unzip(app_pack[0], p('packages/linux'))
    app_dir = p('packages/linux/openLCA')

    # copy ini and licenses
    shutil.copy2(p('templates/openLCA_Linux.ini'), p(app_dir + '/openLCA.ini'))
    shutil.copy2(p('legal/OPENLCA_README.txt'), app_dir)
    shutil.copytree(p('legal/licenses'), p(app_dir + '/licenses'))

    # package the JRE
    jre_tar = glob.glob('runtime/jre/jre-*-linux-x64.tar')
    if len(jre_tar) == 0:
        print('ERROR: no JRE for Linux found')
        return
    unzip(jre_tar[0], app_dir)
    jre_dir = glob.glob(app_dir + '/*jre*')
    os.rename(jre_dir[0], p(app_dir + '/jre'))

    targz('.\\packages\\linux\\*',
          p('packages/openLCA_linux_' + version_date))
    shutil.rmtree(pack_dir)


def pack_macos(version_date):
    app_pack = glob.glob('builds/openlca_*-macosx.cocoa.x86_64.zip')
    if len(app_pack) == 0:
        print('ERROR: could not find macOS package')
        return
    unzip(app_pack[0], p('packages/macos'))
    # TODO: delete `p2/*/.lock` files
    app_dir = p('packages/macos/openLCA/openLCA.app')
    moves = ['configuration', 'p2', 'plugins', '.eclipseproduct',
             'artifacts.xml']
    for m in moves:
        move(p('packages/macos/openLCA/' + m), app_dir)

    # package the JRE
    jre_tar = glob.glob('runtime/jre/jre-*-macosx-x64.tar')
    if len(jre_tar) == 0:
        print('ERROR: no JRE for Mac OSX found')
        return
    unzip(jre_tar[0], app_dir)
    jre_dir = glob.glob(app_dir + '/*jre*')
    os.rename(jre_dir[0], p(app_dir + '/jre'))

    # write the ini file
    launcher_jar = os.path.basename(
        glob.glob(app_dir + '/plugins/*launcher*.jar')[0])
    launcher_lib = os.path.basename(
        glob.glob(app_dir + '/plugins/*launcher.cocoa.macosx*')[0])
    ini = fill_template(p('templates/openLCA_macOS.ini'),
                        launcher_jar=launcher_jar, launcher_lib=launcher_lib)
    ini_file = p(app_dir + '/Contents/MacOS/openLCA.ini')
    with(open(ini_file, 'w', encoding='utf-8', newline='\n')) as f:
        f.write(ini.replace('\r\n', '\n'))

    # create the distribution package
    targz('.\\packages\\macos\\openLCA\\*',
          p('packages/openLCA_macOS_' + version_date))
    shutil.rmtree(pack_dir)


def unzip(zip_file, to_dir):
    """ Extracts the given file to the given folder using 7zip."""
    print('unzip %s to %s' % (zip_file, to_dir))
    if not os.path.exists(to_dir):
        os.makedirs(to_dir)
    zip_app = p('7zip/7za.exe')
    cmd = [zip_app, 'x', zip_file, '-o%s' % to_dir]
    code = subprocess.call(cmd)
    print(code)


def move(f_path, target_dir):
    """ Moves the given file or directory to the given folder. """
    if not os.path.exists(f_path):
        print('File or folder %s does not exist' % f_path)
        return
    print('Move %s to %s' % (f_path, target_dir))
    if not os.path.exists(target_dir):
        os.makedirs(target_dir)
    shutil.move(f_path, target_dir)
    # cmd = ['move', f_path, target_dir]
    # code = subprocess.call(cmd)


def check(code, msg):
    if code != 0:
        print('ERROR: exit code=%s; %s', msg)


def targz(folder, tar_file):
    print('targz %s to %s' % (folder, tar_file))
    tar_app = p('7zip/7za.exe')
    cmd = [tar_app, 'a', '-ttar', tar_file + '.tar', folder]
    subprocess.call(cmd)
    cmd = [tar_app, 'a', '-tgzip', tar_file + '.tar.gz', tar_file + '.tar']
    subprocess.call(cmd)


def get_version():
    version = ''
    with open('build.properties', 'r', encoding='utf-8') as f:
        for line in f:
            text = line.strip()
            if not text.startswith('openlca_version_id'):
                continue
            version = text.split('=')[1].strip()
            break
    return version


def fill_template(file_path, **kwargs):
    with open(file_path, mode='r', encoding='utf-8') as f:
        text = f.read()
        return text.format(**kwargs)


def p(path):
    """ Joins the given strings to a path """
    if os.sep != '/':
        return path.replace('/', os.sep)
    return path


if __name__ == '__main__':
    main()
