package com.korres.controller.admin;

import javax.annotation.Resource;
import com.korres.entity.Member;
import com.korres.service.DepositService;
import com.korres.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.korres.Pageable;

@Controller("adminDepositController")
@RequestMapping({"/admin/deposit"})
public class DepositController extends BaseController
{

  @Resource(name="depositServiceImpl")
  private DepositService depositService;

  @Resource(name="memberServiceImpl")
  private MemberService memberService;

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Long memberId, Pageable pageable, ModelMap model)
  {
    Member localMember = (Member)this.memberService.find(memberId);
    if (localMember != null)
    {
      model.addAttribute("member", localMember);
      model.addAttribute("page", this.depositService.findPage(localMember, pageable));
    }
    else
    {
      model.addAttribute("page", this.depositService.findPage(pageable));
    }
    return "/admin/deposit/list";
  }
}