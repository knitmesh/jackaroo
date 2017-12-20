#!/usr/bin/python2
import argparse
import distutils.errors
import os
import sys
import subprocess

from jinja2 import Template
from urllib import splitport


BASE_DIR = os.path.dirname(os.path.dirname(__file__))
GIT_REPO_DIR = os.path.join(BASE_DIR, ".git")
CONFIG_DIR = os.path.join(BASE_DIR, "conf")
DOCKERFILE = os.path.join(BASE_DIR, "Dockerfile")
DOCKERFILE_TPL = os.path.join(BASE_DIR, "Dockerfile.j2")
DOCKER_ENDPOINT = os.path.join(BASE_DIR, "docker-entrypoint.sh")
DOCKER_ENDPOINT_TPL = os.path.join(BASE_DIR, "docker-entrypoint.sh.j2")
PIP_CONF = os.path.join(CONFIG_DIR, "pip.conf")
PIP_CONF_TPL = os.path.join(CONFIG_DIR, "pip.conf.j2")
YUM_CONF = os.path.join(CONFIG_DIR, "yum.repo")
YUM_CONF_TPL = os.path.join(CONFIG_DIR, "yum.repo.j2")


class Prepare:
    parser = None

    def __init__(self):
        parser = argparse.ArgumentParser(description='Prepare Portal Install')
        parser.add_argument("-e", "--env", default="prod",
                            help="Set server target, product or develop",
                            choices=['prod', 'dev'])
        parser.add_argument("-v", "--version",
                            help="Set portal version. Default from git")
        parser.add_argument("--pip-server",
                            help="Set PyPI server, ServerName or ServerName:Port")
        parser.add_argument("--yum-repo",
                            help="Set yum repo server, http://ServerName[:Port]/centos7 or file:///opt/centos7")
        parser.add_argument("--deploy",
                            help="Set docker DEPLOY environment")
        self.parser = parser

    @staticmethod
    def render(tpl, dest, **kwargs):
        with open(tpl, 'r') as fr:
            template = Template(fr.read())
            with open(dest, 'w') as fw:
                fw.write(template.render(**kwargs))
        print("Generated configuration file: %s" % dest)

    @staticmethod
    def _run_shell_command(cmd, throw_on_error=False, buffer=True, env=None):
        if buffer:
            out_location = subprocess.PIPE
            err_location = subprocess.PIPE
        else:
            out_location = None
            err_location = None

        newenv = os.environ.copy()
        if env:
            newenv.update(env)

        output = subprocess.Popen(cmd,
                                  stdout=out_location,
                                  stderr=err_location,
                                  env=newenv)
        out = output.communicate()
        if output.returncode and throw_on_error:
            raise distutils.errors.DistutilsError(
                "%s returned %d" % (cmd, output.returncode))
        if len(out) == 0 or not out[0] or not out[0].strip():
            return ''
        return out[0].strip().decode('utf-8', 'replace')

    def get_version(self, version):
        if version:
            return version
        return self._run_shell_command([
            'git',
            '--git-dir=%s' % GIT_REPO_DIR,
            'rev-parse',
            '--short',
            'HEAD'
        ])

    @staticmethod
    def get_host_name(host):
        return splitport(host)[0]

    def docker_file(self, args):
        kwargs = dict()
        kwargs["VERSION"] = self.get_version(args.version)
        kwargs["ENVIRONMENT"] = args.env
        kwargs["DEPLOY"] = args.deploy
        kwargs["PIP_SERVER"] = args.pip_server
        kwargs["YUM_REPO"] = args.yum_repo
        self.render(DOCKERFILE_TPL, DOCKERFILE, **kwargs)
        self.render(DOCKER_ENDPOINT_TPL, DOCKER_ENDPOINT, **kwargs)

    def pip_conf_file(self, args):
        if not args.pip_server:
            return
        kwargs = dict()
        kwargs["SERVER_NAME"] = args.pip_server
        kwargs["HOST_NAME"] = self.get_host_name(args.pip_server)
        self.render(PIP_CONF_TPL, PIP_CONF, **kwargs)

    def yum_repo_file(self, args):
        if not args.yum_repo:
            return
        kwargs = dict()
        kwargs["YUM_REPO"] = args.yum_repo
        self.render(YUM_CONF_TPL, YUM_CONF, **kwargs)

    def run(self, argv):
        args = self.parser.parse_args(argv)
        self.docker_file(args)
        self.pip_conf_file(args)
        self.yum_repo_file(args)


def main(argv=sys.argv[1:]):
    app = Prepare()
    app.run(argv)


if __name__ == '__main__':
    sys.exit(main())
